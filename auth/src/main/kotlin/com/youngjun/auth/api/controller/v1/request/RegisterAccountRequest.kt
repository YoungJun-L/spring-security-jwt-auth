package com.youngjun.auth.api.controller.v1.request

data class RegisterAccountRequest(
    val username: String,
    val password: String,
) {
    init {
        require(username.length in 8..<50) { "Username validation error" }
        require(password.length in 8..<65) { "Password validation error" }
    }
}
