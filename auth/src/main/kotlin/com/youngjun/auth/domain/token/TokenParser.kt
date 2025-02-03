package com.youngjun.auth.domain.token

import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.TOKEN_NOT_FOUND_ERROR
import org.springframework.stereotype.Component

@Component
class TokenParser(
    private val jwtParser: JwtParser,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun parse(rawAccessToken: RawAccessToken): ParsedAccessToken = ParsedAccessToken.of(rawAccessToken, jwtParser.parse(rawAccessToken))

    fun parse(rawRefreshToken: RawRefreshToken): ParsedRefreshToken {
        val parsedRefreshToken = ParsedRefreshToken.of(rawRefreshToken, jwtParser.parse(rawRefreshToken))
        val refreshToken =
            refreshTokenRepository.findBy(parsedRefreshToken) ?: throw AuthException(TOKEN_NOT_FOUND_ERROR)
        refreshToken.verify()
        return parsedRefreshToken
    }
}
