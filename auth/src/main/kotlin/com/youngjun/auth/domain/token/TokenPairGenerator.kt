package com.youngjun.auth.domain.token

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TokenPairGenerator(
    private val jwtGenerator: JwtGenerator,
) {
    fun generate(
        userId: Long,
        now: LocalDateTime = LocalDateTime.now(),
    ): TokenPair = TokenPair(userId, jwtGenerator.generateAccessToken(userId, now), jwtGenerator.generateRefreshToken(userId, now))

    fun generateOnExpiration(
        parsedRefreshToken: ParsedRefreshToken,
        now: LocalDateTime = LocalDateTime.now(),
    ): TokenPair =
        TokenPair(
            parsedRefreshToken.userId,
            jwtGenerator.generateAccessToken(parsedRefreshToken.userId, now),
            jwtGenerator.generateRefreshTokenOnExpiration(parsedRefreshToken, now),
        )
}
