package com.youngjun.auth.api.controller.v1.request

data class RegisterAccountRequest(
    val username: String,
    val password: String,
) {
    init {
        require(usernameRegex.matches(username)) { "Username validation error" }
        require(passwordRegex.matches(password)) { "Password validation error" }
    }

    companion object {
        private val usernameRegex = Regex("^(?=.*[a-zA-Z])(?=.*\\d)\\w{8,49}$")
        private val passwordRegex = Regex("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#\$%^*()+=-])[\\w!@#\$%^*()+=-]{10,49}$")
    }
}
