package com.youngjun.auth.domain.token

data class TokenPair(
    val userId: Long,
    val accessToken: ParsedAccessToken,
    val refreshToken: ParsedRefreshToken,
)
