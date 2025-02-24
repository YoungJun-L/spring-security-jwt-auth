package com.youngjun.auth.domain.token

import com.youngjun.auth.infra.jwt.JwtParser
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.TOKEN_NOT_FOUND
import org.springframework.stereotype.Component

@Component
class TokenParser(
    private val jwtParser: JwtParser,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun parse(rawAccessToken: RawAccessToken): ParsedAccessToken = ParsedAccessToken.of(rawAccessToken, jwtParser.parse(rawAccessToken))

    fun parse(rawRefreshToken: RawRefreshToken): ParsedRefreshToken =
        ParsedRefreshToken.of(rawRefreshToken, jwtParser.parse(rawRefreshToken)).also {
            refreshTokenRepository.findBy(it)?.verify() ?: throw AuthException(TOKEN_NOT_FOUND)
        }
}
