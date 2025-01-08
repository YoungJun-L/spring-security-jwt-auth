package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class TokenParser(
    @Value("\${spring.security.jwt.secret-key}") secretKey: String,
) {
    private val jwtParser: JwtParser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.toByteArray())).build()

    fun parseSubject(token: String?): String {
        try {
            return jwtParser.parseSignedClaims(token).payload.subject
        } catch (ex: ExpiredJwtException) {
            throw AuthException(ErrorType.TOKEN_EXPIRED_ERROR)
        } catch (ex: Exception) {
            throw AuthException(ErrorType.TOKEN_INVALID_ERROR)
        }
    }

    fun verify(token: RefreshToken) {
        try {
            jwtParser.parseSignedClaims(token.value)
        } catch (ex: ExpiredJwtException) {
            throw AuthException(ErrorType.TOKEN_EXPIRED_ERROR)
        } catch (ex: Exception) {
            throw AuthException(ErrorType.TOKEN_INVALID_ERROR)
        }
    }
}
