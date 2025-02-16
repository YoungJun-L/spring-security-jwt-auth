package com.youngjun.auth.domain.token

import com.youngjun.auth.security.config.JwtProperties
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.TOKEN_EXPIRED
import com.youngjun.auth.support.error.ErrorType.TOKEN_INVALID
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtParser
import io.jsonwebtoken.Jwts
import org.springframework.stereotype.Component

@Component
class JwtParser(
    jwtProperties: JwtProperties,
) {
    private val accessTokenParser: JwtParser = Jwts.parser().verifyWith(jwtProperties.accessSecretKey).build()
    private val refreshTokenParser: JwtParser = Jwts.parser().verifyWith(jwtProperties.refreshSecretKey).build()

    fun parse(rawAccessToken: RawAccessToken): Payload = parseToken(accessTokenParser, rawAccessToken.value)

    fun parse(rawRefreshToken: RawRefreshToken): Payload = parseToken(refreshTokenParser, rawRefreshToken.value)

    private fun parseToken(
        parser: JwtParser,
        token: String,
    ): Payload =
        try {
            Payload.from(parser.parseSignedClaims(token).payload)
        } catch (ex: ExpiredJwtException) {
            throw AuthException(TOKEN_EXPIRED)
        } catch (ex: Exception) {
            throw AuthException(TOKEN_INVALID)
        }
}
