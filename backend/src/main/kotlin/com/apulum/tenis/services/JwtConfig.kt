package com.apulum.tenis.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.JWTPrincipal
import java.util.Date

object JwtConfig {
    private const val SECRET = "apulum-tenis-dev-secret-change-in-production"
    private const val ISSUER = "apulum-tenis"
    private const val AUDIENCE = "apulum-tenis-users"
    private const val EXPIRATION_MS = 1000L * 60 * 60 * 24 * 30

    val algorithm: Algorithm = Algorithm.HMAC256(SECRET)

    fun createToken(userId: Long, email: String, role: String): String =
        JWT.create()
            .withIssuer(ISSUER)
            .withAudience(AUDIENCE)
            .withClaim("userId", userId)
            .withClaim("email", email)
            .withClaim("role", role)
            .withExpiresAt(Date(System.currentTimeMillis() + EXPIRATION_MS))
            .sign(algorithm)

    fun userId(principal: JWTPrincipal): Long? = principal.payload.getClaim("userId").asLong()

    fun role(principal: JWTPrincipal): String? = principal.payload.getClaim("role").asString()
}
