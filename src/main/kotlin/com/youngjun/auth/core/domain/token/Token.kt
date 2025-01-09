package com.youngjun.auth.core.domain.token

data class Token(
    val userId: Long,
    val refreshToken: RefreshToken,
)
