package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.account.EmailAddressBuilder

data class VerificationCodeBuilder(
    val emailAddress: EmailAddress = EmailAddressBuilder().build(),
) {
    fun build(): VerificationCode = VerificationCode.generate(emailAddress)
}
