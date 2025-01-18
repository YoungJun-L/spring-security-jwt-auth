package com.youngjun.auth.core.api.controller.v1.request

import com.youngjun.auth.core.domain.account.NewAccount

data class RegisterAccountRequest(
    val username: String,
    val password: String,
) {
    fun toNewAccount(): NewAccount {
        require(usernameRegex.matches(username)) { "Username validation error" }
        require(passwordRegex.matches(password)) { "Password validation error" }
        return NewAccount(username, password)
    }

    companion object {
        private val usernameRegex = Regex("^(?=.*[a-zA-Z])(?=.*\\d)\\w{8,49}$")
        private val passwordRegex = Regex("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#\$%^*()+=-])[\\w!@#\$%^*()+=-]{10,49}$")
    }
}
