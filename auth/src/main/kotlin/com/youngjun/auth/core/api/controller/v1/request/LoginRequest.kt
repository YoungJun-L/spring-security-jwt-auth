package com.youngjun.auth.core.api.controller.v1.request

data class LoginRequest(
    val username: String,
    val password: String,
)
