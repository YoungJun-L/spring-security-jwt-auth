package com.youngjun.auth.api.controller.v1.request

import com.youngjun.auth.domain.account.Email
import com.youngjun.auth.domain.account.RawPassword

data class RegisterAccountRequest(
    val email: String,
    val password: String,
) {
    fun toEmail(): Email = Email(email)

    fun toRawPassword(): RawPassword = RawPassword(password)
}
