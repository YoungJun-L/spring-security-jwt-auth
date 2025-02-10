package com.youngjun.auth.api.controller.v1.request

data class ChangePasswordRequest(
    val password: String,
) {
    init {
        require(password.length in 8..<65) { "Password validation error" }
    }
}
