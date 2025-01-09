package com.youngjun.auth.core.api.controller.v1.request

import com.youngjun.auth.core.domain.user.NewUser

data class RegisterUserRequest(
    val username: String,
    val password: String,
) {
    fun toNewUser(): NewUser {
        require(usernameRegex.matches(username)) { "Username validation error" }
        require(passwordRegex.matches(password)) { "Password validation error" }
        return NewUser(username, password)
    }

    companion object {
        private val usernameRegex = Regex("^(?!.*\\s)(?=.*[a-zA-Z])(?=.*\\d).{8,49}$")
        private val passwordRegex = Regex("^(?!.*\\s)(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^*+=-]).{10,49}$")
    }
}
