package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.api.security.InvalidTokenException
import com.youngjun.auth.core.domain.account.Account
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_INVALID_ERROR
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.stereotype.Component
import java.util.Date
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@Component
class TokenProvider(
    private val secretKeyHolder: SecretKeyHolder,
    private val timeHolder: TimeHolder = TimeHolder.Default,
    private val accessExpiresIn: Long = 2.hours.inWholeMilliseconds,
    private val refreshExpiresIn: Long = 1.days.inWholeMilliseconds,
) {
    private val jwtParser: JwtParser = Jwts.parser().verifyWith(secretKeyHolder.get()).build()

    fun parseSubject(token: String): String {
        try {
            return jwtParser.parseSignedClaims(token).payload.subject
        } catch (ex: ExpiredJwtException) {
            throw CredentialsExpiredException("Token is expired", ex)
        } catch (ex: Exception) {
            throw InvalidTokenException("Invalid token", ex)
        }
    }

    fun verify(token: RefreshToken) {
        try {
            jwtParser.parseSignedClaims(token.value)
        } catch (ex: ExpiredJwtException) {
            throw AuthException(TOKEN_EXPIRED_ERROR)
        } catch (ex: Exception) {
            throw AuthException(TOKEN_INVALID_ERROR)
        }
    }

    fun generate(account: Account): TokenPair {
        val now = timeHolder.now()
        val (accessToken, accessTokenExpiration) = buildJwt(account.username, now, accessExpiresIn)
        val (refreshToken, refreshTokenExpiration) = buildJwt(account.username, now, refreshExpiresIn)
        return TokenPair(account.id, accessToken, accessTokenExpiration, refreshToken, refreshTokenExpiration)
    }

    private fun buildJwt(
        subject: String,
        issuedAt: Long,
        expiresInMilliseconds: Long,
        extraClaims: Map<String, Any> = emptyMap(),
    ): Pair<String, Long> {
        val expiration = issuedAt + expiresInMilliseconds
        return Jwts
            .builder()
            .subject(subject)
            .issuedAt(Date(issuedAt))
            .expiration(Date(expiration))
            .claims(extraClaims)
            .signWith(secretKeyHolder.get())
            .compact() to expiration
    }
}
