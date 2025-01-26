package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.api.security.JwtProperties
import com.youngjun.auth.core.domain.account.Account
import com.youngjun.auth.core.domain.support.toInstant
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.LocalDateTime
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtGenerator(
    private val jwtProperties: JwtProperties,
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    fun generateAccessToken(account: Account): ParsedAccessToken {
        val now = LocalDateTime.now(clock)
        val expiration = now + jwtProperties.accessTokenExpiresIn
        return ParsedAccessToken(
            buildJwt(account, now, expiration, jwtProperties.accessSecretKey),
            Payload(account.id, expiration),
        )
    }

    fun generateRefreshToken(account: Account): ParsedRefreshToken {
        val now = LocalDateTime.now(clock)
        val expiration = now + jwtProperties.refreshTokenExpiresIn
        return ParsedRefreshToken(
            buildJwt(account, now, expiration, jwtProperties.refreshSecretKey),
            Payload(account.id, expiration),
        )
    }

    fun reissueRefreshToken(
        account: Account,
        refreshTokenExpiration: LocalDateTime,
    ): ParsedRefreshToken {
        fun isExpiringSoon(now: LocalDateTime) =
            now in (refreshTokenExpiration - jwtProperties.expirationThreshold..<refreshTokenExpiration)

        val now = LocalDateTime.now(clock)
        return if (isExpiringSoon(now)) {
            val expiration = now + jwtProperties.refreshTokenExpiresIn
            ParsedRefreshToken(
                buildJwt(account, now, expiration, jwtProperties.refreshSecretKey),
                Payload(account.id, expiration),
            )
        } else {
            ParsedRefreshToken.Empty
        }
    }

    private fun buildJwt(
        account: Account,
        issuedAt: LocalDateTime,
        expiration: LocalDateTime,
        secretKey: SecretKey,
        extraClaims: Map<String, Any> = emptyMap(),
    ): String =
        Jwts
            .builder()
            .subject(account.id.toString())
            .issuedAt(Date.from(issuedAt.toInstant()))
            .expiration(Date.from(expiration.toInstant()))
            .claims(extraClaims)
            .signWith(secretKey)
            .compact()
}
