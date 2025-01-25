package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.api.security.JwtProperties
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_INVALID_ERROR
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component

@Component
class TokenParser(
    jwtProperties: JwtProperties,
) {
    private val accessTokenParser: JwtParser = Jwts.parser().verifyWith(jwtProperties.accessSecretKey).build()
    private val refreshTokenParser: JwtParser = Jwts.parser().verifyWith(jwtProperties.refreshSecretKey).build()

    fun parseUserId(accessToken: AccessToken) =
        try {
            accessTokenParser
                .parseSignedClaims(accessToken.value)
                .payload.subject
                .toLong()
        } catch (ex: ExpiredJwtException) {
            throw AuthException(TOKEN_EXPIRED_ERROR)
        } catch (ex: Exception) {
            throw AuthException(TOKEN_INVALID_ERROR)
        }

    fun parseUserId(refreshToken: RefreshToken) =
        try {
            refreshTokenParser
                .parseSignedClaims(refreshToken.value)
                .payload.subject
                .toLong()
        } catch (ex: ExpiredJwtException) {
            throw AuthException(TOKEN_EXPIRED_ERROR)
        } catch (ex: Exception) {
            throw AuthException(TOKEN_INVALID_ERROR)
        }
}
