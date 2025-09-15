package br.com.iasmin.iasminasteriskari.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class JwtServiceTest {

    private val secret = "test-secret"
    private val jwtService = JwtService(secret)

    @Test
    fun `deve retornar true quando token for valido e tiver a claim action = call`() {
        val token = JWT.create()
            .withClaim("action", "call")
            .sign(Algorithm.HMAC256(secret))

        val result = jwtService.validateCallToken(token)
        assertTrue(result, "Esperado token valido com claim 'action = call'")
    }

    @Test
    fun `deve retornar false quando token for valido mas nao tiver a claim action = call`() {
        val token = JWT.create()
            .withClaim("other", "value")
            .sign(Algorithm.HMAC256(secret))

        val result = jwtService.validateCallToken(token)
        assertFalse(result, "Esperado token valido com claim action inexistente ou diferente de 'call'")
    }

    @Test
    fun `deve retornar false para token assinado com segredo errado`() {
        val token = JWT.create()
            .sign(Algorithm.HMAC256("wrong-secret"))

        val result = jwtService.validateCallToken(token)
        assertFalse(result, "Esperado token invalido com segredo diferente")
    }

    @Test
    fun `deve retornar false para token vazio ou com apenas espaços`() {
        assertFalse(jwtService.validateCallToken(""))
        assertFalse(jwtService.validateCallToken("   "))
    }
}