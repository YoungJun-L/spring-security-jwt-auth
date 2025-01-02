package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.domain.auth.Auth
import com.youngjun.auth.core.domain.support.TimeHolder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant.now
import java.util.Date

@Component
class TokenPairGenerator(
    @Value("\${spring.security.jwt.secret-key}") private val secretKey: String,
    @Value("\${spring.security.jwt.exp.access}") private val accessExp: Long,
    @Value("\${spring.security.jwt.exp.refresh}") private val refreshExp: Long,
    private val timeHolder: TimeHolder = TimeHolder { now().toEpochMilli() },
) {
    fun issue(auth: Auth): TokenPair {
        val now = timeHolder.now()
        val subject = auth.username
        val accessToken = generateAccessToken(subject, now)
        val refreshToken = generateRefreshToken(subject, now)
        return TokenPair(auth.id, accessToken, now + accessExp, refreshToken, now + refreshExp)
    }

    private fun generateAccessToken(
        subject: String,
        issuedAt: Long,
    ): String = buildToken(emptyMap(), subject, issuedAt, accessExp)

    private fun generateRefreshToken(
        subject: String,
        issuedAt: Long,
    ): String = buildToken(emptyMap(), subject, issuedAt, refreshExp)

    private fun buildToken(
        extraClaims: Map<String, Any>,
        subject: String,
        issuedAt: Long,
        expiration: Long,
    ): String =
        Jwts
            .builder()
            .claims(extraClaims)
            .subject(subject)
            .issuedAt(Date(issuedAt))
            .expiration(Date(issuedAt + expiration))
            .signWith(Keys.hmacShaKeyFor(secretKey.toByteArray()))
            .compact()
}
