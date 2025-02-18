package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.support.toInstant
import com.youngjun.auth.security.config.JwtProperties
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDateTime
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtGenerator(
    private val jwtProperties: JwtProperties,
    private val clock: Clock,
) {
    fun generateAccessToken(userId: Long): ParsedAccessToken {
        val now = LocalDateTime.now(clock)
        val expiration = now + jwtProperties.accessTokenExpiresIn
        return ParsedAccessToken(buildJwt(userId, now, expiration, jwtProperties.accessSecretKey), Payload(userId, expiration))
    }

    fun generateRefreshToken(userId: Long): ParsedRefreshToken {
        val now = LocalDateTime.now(clock)
        val expiration = now + jwtProperties.refreshTokenExpiresIn
        return ParsedRefreshToken(buildJwt(userId, now, expiration, jwtProperties.refreshSecretKey), Payload(userId, expiration))
    }

    fun generateRefreshTokenOnExpiration(
        userId: Long,
        refreshTokenExpiration: LocalDateTime,
    ): ParsedRefreshToken {
        fun isExpiringSoon(now: LocalDateTime) =
            now in (refreshTokenExpiration - jwtProperties.expirationThreshold..<refreshTokenExpiration)

        val now = LocalDateTime.now(clock)
        return if (isExpiringSoon(now)) {
            val expiration = now + jwtProperties.refreshTokenExpiresIn
            ParsedRefreshToken(buildJwt(userId, now, expiration, jwtProperties.refreshSecretKey), Payload(userId, expiration))
        } else {
            ParsedRefreshToken.Empty
        }
    }

    private fun buildJwt(
        userId: Long,
        issuedAt: LocalDateTime,
        expiration: LocalDateTime,
        secretKey: SecretKey,
        extraClaims: Map<String, Any> = emptyMap(),
    ): String =
        Jwts
            .builder()
            .subject(userId.toString())
            .issuedAt(Date.from(issuedAt.toInstant()))
            .expiration(Date.from(expiration.toInstant()))
            .claims(extraClaims)
            .signWith(secretKey)
            .compact()
}
