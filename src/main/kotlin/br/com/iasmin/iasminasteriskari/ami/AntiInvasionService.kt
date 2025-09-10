package br.com.iasmin.iasminasteriskari.ami

import org.asteriskjava.manager.event.InvalidAccountId
import org.springframework.stereotype.Service
import java.time.LocalDateTime

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 27/06/2023
 */
@Service
class AntiInvasionService {

    private val invaders = mutableMapOf<String, Invader>()
    private val blockedInvaders = mutableMapOf<String, Invader>()

    fun antiInvasion(invalidAccountId: InvalidAccountId) {
        val ip = invalidAccountId.remoteAddress.split("/")[2]
        if (invaders.containsKey(ip)) {
            blockOrSumInvader(invaders.getValue(ip))
            return
        }
        invaders[ip] = Invader(invalidAccountId)
    }

    private fun blockOrSumInvader(invader: Invader) {
        if (invader.attempts > 5) {
            if (LocalDateTime.now().minusMinutes(1).isBefore(invader.firstAttempt)) {
                blockedInvaders[invader.ip] = invader
                return
            }
            invaders.remove(invader.ip)
            return
        }
        invaders[invader.ip] = invader.copy(attempts = invader.attempts + 1)
    }
}
