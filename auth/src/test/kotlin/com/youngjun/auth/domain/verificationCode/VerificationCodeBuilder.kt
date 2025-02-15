package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.account.EmailAddressBuilder

data class VerificationCodeBuilder(
    val emailAddress: EmailAddress = EmailAddressBuilder().build(),
    val code: Int = 123456,
    val isVerified: Boolean = false,
) {
    fun build(): VerificationCode =
        VerificationCode(
            emailAddress = emailAddress,
            code = code,
            isVerified = isVerified,
        )
}
