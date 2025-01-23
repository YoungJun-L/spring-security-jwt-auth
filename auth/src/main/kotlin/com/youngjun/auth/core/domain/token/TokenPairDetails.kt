package com.youngjun.auth.core.domain.token

import java.time.LocalDateTime

data class TokenPairDetails(
    val userId: Long,
    val accessToken: AccessToken,
    val accessTokenExpiration: LocalDateTime,
    val refreshToken: RefreshToken,
    val refreshTokenExpiration: LocalDateTime,
)
