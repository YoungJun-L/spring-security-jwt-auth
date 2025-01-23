package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.domain.account.Account
import com.youngjun.auth.core.domain.support.hours
import com.youngjun.auth.core.domain.support.toInstant
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.time.Clock
import java.time.Duration
import java.time.LocalDateTime
import java.util.Date

@Component
class TokenGenerator(
    private val secretKeyHolder: SecretKeyHolder,
    private val accessTokenExpiresIn: Duration = 1.hours,
    private val refreshTokenExpiresIn: Duration = 12.hours,
    private val clock: Clock = Clock.systemDefaultZone(),
) {
    fun generate(account: Account): TokenPairDetails {
        val now = LocalDateTime.now(clock)
        val (accessToken, accessTokenExpiration) = buildJwt(account, now, accessTokenExpiresIn)
        val (refreshToken, refreshTokenExpiration) = buildJwt(account, now, refreshTokenExpiresIn)
        return TokenPairDetails(
            account.id,
            AccessToken(accessToken),
            accessTokenExpiration,
            RefreshToken(refreshToken),
            refreshTokenExpiration,
        )
    }

    private fun buildJwt(
        account: Account,
        issuedAt: LocalDateTime,
        expiresIn: Duration,
        extraClaims: Map<String, Any> = emptyMap(),
    ): Pair<String, LocalDateTime> {
        val expiration = issuedAt + expiresIn
        return Jwts
            .builder()
            .subject(account.id.toString())
            .issuedAt(Date.from(issuedAt.toInstant()))
            .expiration(Date.from(expiration.toInstant()))
            .claims(extraClaims)
            .signWith(secretKeyHolder.get())
            .compact() to expiration
    }
}
