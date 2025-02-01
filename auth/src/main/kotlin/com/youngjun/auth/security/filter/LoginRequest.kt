package com.youngjun.auth.security.filter

data class LoginRequest(
    val username: String,
    val password: String,
)
