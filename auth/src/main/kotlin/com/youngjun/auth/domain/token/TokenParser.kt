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
    fun parse(rawAccessToken: RawAccessToken): ParsedAccessToken = jwtParser.parse(rawAccessToken)

    fun parse(rawRefreshToken: RawRefreshToken): ParsedRefreshToken = jwtParser.parse(rawRefreshToken).also { read(it).verify() }

    private fun read(parsedRefreshToken: ParsedRefreshToken): RefreshToken =
        refreshTokenRepository.findByUserId(parsedRefreshToken.userId)?.takeIf { it.matches(parsedRefreshToken.rawToken) }
            ?: throw AuthException(TOKEN_NOT_FOUND)
}
