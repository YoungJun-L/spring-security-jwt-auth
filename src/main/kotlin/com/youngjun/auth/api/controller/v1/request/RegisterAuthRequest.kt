package com.youngjun.auth.api.controller.v1.request

import com.youngjun.auth.core.domain.auth.AuthStatus
import com.youngjun.auth.core.domain.auth.NewAuth

data class RegisterAuthRequest(
    val username: String,
    val password: String,
) {
    fun toNewAuth(): NewAuth {
        require(usernameRegex.matches(username)) { "Username validation error" }
        require(passwordRegex.matches(password)) { "Password validation error" }
        return NewAuth(username, password, AuthStatus.ENABLED)
    }

    companion object {
        private val usernameRegex = Regex("^(?!.*\\s)(?=.*[a-zA-Z])(?=.*\\d).{8,49}$")
        private val passwordRegex = Regex("^(?!.*\\s)(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^*+=-]).{10,49}$")
    }
}
