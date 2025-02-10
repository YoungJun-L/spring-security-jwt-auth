package com.youngjun.auth.api.controller.v1.request

import com.youngjun.auth.domain.account.NewAccount

data class RegisterAccountRequest(
    val username: String,
    val password: String,
) {
    init {
        require(usernameRegex.matches(username)) { "Username validation error" }
        require(passwordRegex.matches(password)) { "Password validation error" }
    }

    fun toNewAccount(): NewAccount = NewAccount(username, password)

    companion object {
        private val usernameRegex = Regex("^(?=.*[a-zA-Z])(?=.*\\d)\\w{8,49}$")
        private val passwordRegex = Regex("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#\$%^*()+=-])[\\w!@#\$%^*()+=-]{10,49}$")
    }
}
