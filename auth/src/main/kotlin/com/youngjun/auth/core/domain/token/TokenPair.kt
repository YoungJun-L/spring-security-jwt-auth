package com.youngjun.auth.core.domain.token

import java.time.LocalDateTime

data class TokenPair(
    val userId: Long,
    val accessToken: String,
    val accessTokenExpiration: LocalDateTime,
    val refreshToken: String,
    val refreshTokenExpiration: LocalDateTime,
)
