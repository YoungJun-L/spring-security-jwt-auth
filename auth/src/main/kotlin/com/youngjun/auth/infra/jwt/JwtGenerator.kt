package com.youngjun.auth.infra.jwt

import com.youngjun.auth.domain.support.toInstant
import com.youngjun.auth.domain.token.ParsedAccessToken
import com.youngjun.auth.domain.token.ParsedRefreshToken
import com.youngjun.auth.domain.token.Payload
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
    ): ParsedAccessToken =
        ParsedAccessToken(
            buildJwt(userId, now, now + jwtProperties.accessTokenExpiresIn, jwtProperties.accessSecretKey),
            Payload(
                userId,
                now + jwtProperties.accessTokenExpiresIn,
            ),
        )

    fun generateRefreshToken(
        userId: Long,
        now: LocalDateTime = LocalDateTime.now(),
    ): ParsedRefreshToken =
        ParsedRefreshToken(
            buildJwt(userId, now, now + jwtProperties.refreshTokenExpiresIn, jwtProperties.refreshSecretKey),
            Payload(
                userId,
                now + jwtProperties.refreshTokenExpiresIn,
            ),
        )

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
