package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.support.toInstant
import com.youngjun.auth.security.config.JwtProperties
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtGenerator(
    private val jwtProperties: JwtProperties,
) {
    fun generateAccessToken(
        userId: Long,
        now: LocalDateTime = LocalDateTime.now(),
    ): ParsedAccessToken {
        val expiration = now + jwtProperties.accessTokenExpiresIn
        return ParsedAccessToken(buildJwt(userId, now, expiration, jwtProperties.accessSecretKey), Payload(userId, expiration))
    }

    fun generateRefreshToken(
        userId: Long,
        now: LocalDateTime = LocalDateTime.now(),
    ): ParsedRefreshToken {
        val expiration = now + jwtProperties.refreshTokenExpiresIn
        return ParsedRefreshToken(buildJwt(userId, now, expiration, jwtProperties.refreshSecretKey), Payload(userId, expiration))
    }

    fun generateRefreshTokenOnExpiration(
        parsedRefreshToken: ParsedRefreshToken,
        now: LocalDateTime = LocalDateTime.now(),
    ): ParsedRefreshToken {
        fun isExpiringSoon(now: LocalDateTime) =
            now in (parsedRefreshToken.expiration - jwtProperties.expirationThreshold..<parsedRefreshToken.expiration)

        return if (isExpiringSoon(now)) {
            val expiration = now + jwtProperties.refreshTokenExpiresIn
            ParsedRefreshToken(
                buildJwt(parsedRefreshToken.userId, now, expiration, jwtProperties.refreshSecretKey),
                Payload(parsedRefreshToken.userId, expiration),
            )
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
