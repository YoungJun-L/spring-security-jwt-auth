package com.youngjun.auth.security.filter

import com.youngjun.auth.domain.account.Email

data class LoginRequest(
    val email: String,
    val password: String,
) {
    fun toEmail() = Email(email)
}
