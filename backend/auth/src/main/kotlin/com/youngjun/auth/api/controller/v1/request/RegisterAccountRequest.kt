package com.youngjun.auth.api.controller.v1.request

import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.account.RawPassword
import com.youngjun.auth.domain.verificationCode.RawVerificationCode

data class RegisterAccountRequest(
    val email: EmailAddress,
    val password: RawPassword,
    val verificationCode: RawVerificationCode,
)
