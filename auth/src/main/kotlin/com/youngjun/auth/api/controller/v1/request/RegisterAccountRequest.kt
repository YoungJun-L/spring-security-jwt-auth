package com.youngjun.auth.api.controller.v1.request

import com.youngjun.auth.domain.account.Email

data class RegisterAccountRequest(
    val email: String,
    val password: String,
) {
    init {
        require(password.length in 8..<65) { "Password validation error" }
    }

    fun toEmail(): Email = Email(email)
}
