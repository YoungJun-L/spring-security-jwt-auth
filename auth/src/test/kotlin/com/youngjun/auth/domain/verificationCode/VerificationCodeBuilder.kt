package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.Email
import com.youngjun.auth.domain.account.EmailBuilder

data class VerificationCodeBuilder(
    val email: Email = EmailBuilder().build(),
    val code: Int = 123456,
    val isVerified: Boolean = false,
) {
    fun build(): VerificationCode =
        VerificationCode(
            email = email,
            code = code,
            isVerified = isVerified,
        )
}
