package com.youngjun.auth.core.domain.token

data class TokenPair(
    val authId: Long,
    val accessToken: String,
    val accessTokenExpiresIn: Long,
    val refreshToken: String,
    val refreshTokenExpiresIn: Long,
)
