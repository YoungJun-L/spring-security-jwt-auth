package com.youngjun.auth.core.domain.token

data class TokenPair(
    val authId: Long,
    val accessToken: String,
    val accessTokenExpiration: Long,
    val refreshToken: String,
    val refreshTokenExpiration: Long,
)
