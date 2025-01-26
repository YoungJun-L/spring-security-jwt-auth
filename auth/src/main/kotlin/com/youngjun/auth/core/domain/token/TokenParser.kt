package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_NOT_FOUND_ERROR
import com.youngjun.auth.storage.db.core.token.RefreshTokenRepository
import org.springframework.stereotype.Component

@Component
class TokenParser(
    private val jwtParser: JwtParser,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun parse(rawAccessToken: RawAccessToken): ParsedAccessToken = ParsedAccessToken.of(rawAccessToken, jwtParser.parse(rawAccessToken))

    fun parse(rawRefreshToken: RawRefreshToken): ParsedRefreshToken {
        val parsedRefreshToken = ParsedRefreshToken.of(rawRefreshToken, jwtParser.parse(rawRefreshToken))
        val refreshToken = refreshTokenRepository.read(parsedRefreshToken) ?: throw AuthException(TOKEN_NOT_FOUND_ERROR)
        refreshToken.verify()
        return parsedRefreshToken
    }
}
