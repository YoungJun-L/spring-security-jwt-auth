package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_INVALID_ERROR
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component

@Component
class TokenParser(
    secretKeyHolder: SecretKeyHolder,
) {
    private val jwtParser: JwtParser = Jwts.parser().verifyWith(secretKeyHolder.get()).build()

    fun parseUserId(refreshToken: RefreshToken) = parseUserId(refreshToken.value)

    fun parseUserId(accessToken: AccessToken) = parseUserId(accessToken.value)

    private fun parseUserId(token: String) =
        try {
            jwtParser
                .parseSignedClaims(token)
                .payload.subject
                .toLong()
        } catch (ex: ExpiredJwtException) {
            throw AuthException(TOKEN_EXPIRED_ERROR)
        } catch (ex: Exception) {
            throw AuthException(TOKEN_INVALID_ERROR)
        }
}
