package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.domain.auth.Auth
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.LocalDateTime.now
import java.time.ZoneId
import java.util.Date

@Component
class TokenPairGenerator(
    @Value("\${spring.security.jwt.secret-key}") private val secretKey: String,
    @Value("\${spring.security.jwt.exp.access}") private val accessExpiresIn: Long,
    @Value("\${spring.security.jwt.exp.refresh}") private val refreshExpiresIn: Long,
) {
    fun issue(auth: Auth): TokenPair {
        val now = now()
        val (accessToken, accessTokenExpiration) = buildJwt(auth.username, now, accessExpiresIn)
        val (refreshToken, refreshTokenExpiration) = buildJwt(auth.username, now, refreshExpiresIn)
        return TokenPair(auth.id, accessToken, accessTokenExpiration, refreshToken, refreshTokenExpiration)
    }

    private fun buildJwt(
        subject: String,
        issuedAt: LocalDateTime,
        expiresInSeconds: Long,
        extraClaims: Map<String, Any> = emptyMap(),
    ): Pair<String, Long> {
        val expiration = issuedAt.atZone(ZoneId.systemDefault()).toEpochSecond() + expiresInSeconds
        return Jwts
            .builder()
            .subject(subject)
            .issuedAt(Date(issuedAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
            .expiration(Date(expiration * 1_000))
            .claims(extraClaims)
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .compact() to expiration
    }
}
