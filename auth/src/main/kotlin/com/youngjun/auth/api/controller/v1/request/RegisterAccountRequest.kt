package com.youngjun.auth.api.controller.v1.request

import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.account.RawPassword

data class RegisterAccountRequest(
    val email: String,
    val password: String,
) {
    fun toEmailAddress(): EmailAddress = EmailAddress(email)

    fun toRawPassword(): RawPassword = RawPassword(password)
}
