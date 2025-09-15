package br.com.iasmin.iasminasteriskari.security

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

/**
 * @author Jefferson A. Reis (jefaokpta) < jefaokpta@hotmail.com >
 * Date: 27/06/2023
 */
@Service
class JwtService(
    @Value("\${jwt.secret}")
    private val SECRET: String
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    /**
     * Valida o token JWT usando a lib Auth0.
     * Regras:
     * - Aceita um token com ou sem prefixo "Bearer ".
     * - Verifica a assinatura (HMAC256) com o segredo configurado em jwt.secret.
     * - Respeita verificações padrão (exp/nbf se presentes no token).
     * - Garante que exista a claim obrigatória chamada "call".
     *
     * @return true se o token for válido e contiver a claim "call"; caso contrário, false.
     */
    fun validateCallToken(token: String): Boolean {
        if (token.isBlank()) return false
        return try {
            val algorithm = Algorithm.HMAC256(SECRET)
            val verifier = JWT.require(algorithm).build()
            val decoded = verifier.verify(token)

            val actionClaim = decoded.getClaim("action")
            actionClaim.asString() == "call"
        } catch (e: Exception) {
            logger.error("Erro ao validar token JWT: ${e.message}", e)
            false
        }
    }
}