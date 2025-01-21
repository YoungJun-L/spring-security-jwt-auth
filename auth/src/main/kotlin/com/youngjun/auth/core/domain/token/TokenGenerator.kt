package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.domain.account.Account
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component
import java.util.Date
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@Component
class TokenGenerator(
    private val secretKeyHolder: SecretKeyHolder,
    private val timeHolder: TimeHolder = TimeHolder.Default,
    private val accessExpiresIn: Long = 2.hours.inWholeMilliseconds,
    private val refreshExpiresIn: Long = 1.days.inWholeMilliseconds,
) {
    fun generate(account: Account): TokenPair {
        val now = timeHolder.now()
        val (accessToken, accessTokenExpiration) = buildJwt(account, now, accessExpiresIn)
        val (refreshToken, refreshTokenExpiration) = buildJwt(account, now, refreshExpiresIn)
        return TokenPair(account.id, accessToken, accessTokenExpiration, refreshToken, refreshTokenExpiration)
    }

    private fun buildJwt(
        account: Account,
        issuedAt: Long,
        expiresInMilliseconds: Long,
        extraClaims: Map<String, Any> = emptyMap(),
    ): Pair<String, Long> {
        val expiration = issuedAt + expiresInMilliseconds
        return Jwts
            .builder()
            .subject(account.id.toString())
            .issuedAt(Date(issuedAt))
            .expiration(Date(expiration))
            .claims(extraClaims)
            .signWith(secretKeyHolder.get())
            .compact() to expiration
    }
}
