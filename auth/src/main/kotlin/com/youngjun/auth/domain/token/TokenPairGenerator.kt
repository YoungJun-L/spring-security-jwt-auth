package com.youngjun.auth.domain.token

import com.youngjun.auth.infra.jwt.JwtGenerator
import com.youngjun.auth.infra.jwt.JwtProperties
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class TokenPairGenerator(
    private val jwtGenerator: JwtGenerator,
    private val refreshTokenStore: RefreshTokenStore,
    private val jwtProperties: JwtProperties,
) {
    fun generate(
        userId: Long,
        now: LocalDateTime = LocalDateTime.now(),
    ): TokenPair =
        TokenPair(
            userId,
            jwtGenerator.generateAccessToken(userId, now),
            jwtGenerator.generateRefreshToken(userId, now),
        ).also { refreshTokenStore.replace(it.refreshToken) }

    fun generateOnExpiration(
        parsedRefreshToken: ParsedRefreshToken,
        now: LocalDateTime = LocalDateTime.now(),
    ): TokenPair {
        val generatedRefreshToken =
            if (parsedRefreshToken.isExpiringSoon(now, jwtProperties.expirationThreshold)) {
                jwtGenerator.generateRefreshToken(parsedRefreshToken.userId, now).also { refreshTokenStore.replace(it) }
            } else {
                ParsedRefreshToken.Empty
            }
        return TokenPair(
            parsedRefreshToken.userId,
            jwtGenerator.generateAccessToken(parsedRefreshToken.userId, now),
            generatedRefreshToken,
        )
    }
}
