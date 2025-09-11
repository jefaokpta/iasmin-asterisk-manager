package br.com.iasmin.iasminasteriskari.ami

import org.asteriskjava.manager.event.InvalidAccountId
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.lang.reflect.Field

@Disabled
class AntiInvasionServiceTest {

    @Test
    fun `mais de 5 tentativas deve bloquear o ip`() {
        val service = AntiInvasionService()
        val ipPort = "x/y/10.0.0.1" // will extract index [2] -> 10.0.0.1:5060
        val event = newInvalidAccountId(ipPort)
        val invader = Invader(event)

        // 1st call -> registers invader with attempts=0
        service.antiInvasion(invader)
        // Up to threshold (attempts <= 5) shouldn't be blocked
        repeat(5) { service.antiInvasion(invader) } // now internal attempts will be 5
        assertFalse(isBlocked(service, "10.0.0.1"), "Should not be blocked at 5 attempts")

        // Next call increments to 6 (still not blocking yet according to current logic)
        service.antiInvasion(invader)
        assertFalse(isBlocked(service, "10.0.0.1"), "Should not be blocked immediately when attempts becomes 6")

        // Next call sees attempts > 5 and should block because it's within 1 minute window
        service.antiInvasion(invader)
        assertTrue(isBlocked(service, "10.0.0.1"), "Should be blocked after more than 5 attempts within 1 minute")
    }
    @Test
    fun `mais de 5 tentativas nao deve bloquear se passar mais de 1 minuto`() {
        val service = AntiInvasionService()
        val event = newInvalidAccountId("x/y/10.0.0.1")
        val invader = Invader(event)
        service.antiInvasion(invader)
        service.antiInvasion(invader)
        service.antiInvasion(invader)
        service.antiInvasion(invader)
        service.antiInvasion(invader)
        assertFalse(isBlocked(service, "10.0.0.1"), "Should not be blocked immediately when attempts becomes 6")
        Thread.sleep(61000)
        service.antiInvasion(invader)
        assertFalse(isBlocked(service, "10.0.0.1"), "nao deve ser bloqueado se passar mais de 1 minuto")
        service.antiInvasion(invader)
        assertFalse(isBlocked(service, "10.0.0.1"), "nao deve ser bloqueado se passar mais de 1 minuto")
    }

    private fun isBlocked(service: AntiInvasionService, key: String): Boolean {
        val f: Field = service.javaClass.getDeclaredField("blockedInvaders")
        f.isAccessible = true
        @Suppress("UNCHECKED_CAST")
        val blocked = f.get(service) as MutableMap<String, Invader>
        return blocked.containsKey(key)
    }

    private fun newInvalidAccountId(remoteAddress: String): InvalidAccountId {
        val event = InvalidAccountId(Any())
        // Try the public setter first
        try {
            val m = event.javaClass.getMethod("setRemoteAddress", String::class.java)
            m.invoke(event, remoteAddress)
            return event
        } catch (_: Throwable) { }

        // Fallback to reflection on field name if needed
        return try {
            val field = event.javaClass.getDeclaredField("remoteAddress")
            field.isAccessible = true
            field.set(event, remoteAddress)
            event
        } catch (e: Throwable) {
            throw RuntimeException("Could not set remoteAddress on InvalidAccountId for testing", e)
        }
    }
}
