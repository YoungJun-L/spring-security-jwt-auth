package com.youngjun.auth.api.controller.v1.request

import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.account.RawPassword
import com.youngjun.auth.domain.verificationCode.RawVerificationCode

data class RegisterAccountRequest(
    val email: String,
    val password: String,
    val verificationCode: String,
) {
    fun toEmailAddress(): EmailAddress = EmailAddress.from(email)

    fun toRawPassword(): RawPassword = RawPassword(password)

    fun toRawVerificationCode(): RawVerificationCode = RawVerificationCode(verificationCode)
}
