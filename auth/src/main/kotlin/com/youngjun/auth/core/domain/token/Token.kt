package com.youngjun.auth.core.domain.token

data class Token(
    val id: Long,
    val userId: Long,
    val refreshToken: String,
    val status: TokenStatus,
)
