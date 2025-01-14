package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.api.security.InvalidTokenException
import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import com.youngjun.auth.core.api.support.error.ErrorType.TOKEN_INVALID_ERROR
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.stereotype.Component

@Component
class TokenParser(
    @Value("\${spring.security.jwt.secret-key}") private val secretKey: String,
) {
    private val jwtParser: JwtParser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.toByteArray())).build()

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
}
