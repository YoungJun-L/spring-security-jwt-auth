package com.youngjun.auth.domain.token

import org.springframework.stereotype.Component

@Component
class TokenPairGenerator(
    private val jwtGenerator: JwtGenerator,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun generate(userId: Long): TokenPair =
        TokenPair(userId, jwtGenerator.generateAccessToken(userId), jwtGenerator.generateRefreshToken(userId))

    fun reissue(parsedRefreshToken: ParsedRefreshToken): TokenPair =
        TokenPair(
            parsedRefreshToken.userId,
            jwtGenerator.generateAccessToken(parsedRefreshToken.userId),
            jwtGenerator.generateRefreshTokenOnExpiration(parsedRefreshToken.userId, parsedRefreshToken.expiration),
        ).also {
            if (it.refreshToken.exists()) {
                refreshTokenRepository.update(it.refreshToken)
            }
        }
}
