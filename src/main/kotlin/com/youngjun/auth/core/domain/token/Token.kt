package com.youngjun.auth.core.domain.token

data class Token(
    val authId: Long,
    val refreshToken: RefreshToken,
)
