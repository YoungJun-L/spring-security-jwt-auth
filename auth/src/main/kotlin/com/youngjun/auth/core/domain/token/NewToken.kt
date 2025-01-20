package com.youngjun.auth.core.domain.token

data class NewToken(
    val userId: Long,
    val refreshToken: String,
)
