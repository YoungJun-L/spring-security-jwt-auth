package com.youngjun.auth.domain.token

import org.springframework.stereotype.Component

@Component
class TokenPairGenerator(
    private val jwtGenerator: JwtGenerator,
) {
    fun generate(userId: Long): TokenPair =
        TokenPair(userId, jwtGenerator.generateAccessToken(userId), jwtGenerator.generateRefreshToken(userId))

    fun generateOnExpiration(parsedRefreshToken: ParsedRefreshToken): TokenPair =
        TokenPair(
            parsedRefreshToken.userId,
            jwtGenerator.generateAccessToken(parsedRefreshToken.userId),
            jwtGenerator.generateRefreshTokenOnExpiration(parsedRefreshToken.userId, parsedRefreshToken.expiration),
        )
}
