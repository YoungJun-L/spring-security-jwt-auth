package com.youngjun.auth.api.controller.v1.request

data class LoginRequest(
    val username: String,
    val password: String,
)
