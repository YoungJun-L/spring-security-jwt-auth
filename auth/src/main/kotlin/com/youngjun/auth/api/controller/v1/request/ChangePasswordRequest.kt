package com.youngjun.auth.api.controller.v1.request

data class ChangePasswordRequest(
    val password: String,
) {
    init {
        require(passwordRegex.matches(password)) { "Password validation error" }
    }

    companion object {
        private val passwordRegex = Regex("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#\$%^*()+=-])[\\w!@#\$%^*()+=-]{10,49}$")
    }
}
