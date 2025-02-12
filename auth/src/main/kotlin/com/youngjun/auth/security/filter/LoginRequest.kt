package com.youngjun.auth.security.filter

import com.youngjun.auth.domain.account.Email
import com.youngjun.auth.domain.account.RawPassword

data class LoginRequest(
    val email: String,
    val password: String,
) {
    fun toEmail(): Email = Email(email)

    fun toRawPassword(): RawPassword = RawPassword(password)
}
