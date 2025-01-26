package com.youngjun.auth.core.domain.token

data class TokenPair(
    val userId: Long,
    val accessToken: ParsedAccessToken,
    val refreshToken: ParsedRefreshToken,
)
