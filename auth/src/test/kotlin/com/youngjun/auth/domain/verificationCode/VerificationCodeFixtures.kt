package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.account.EmailAddressBuilder

data class RawVerificationCodeBuilder(
    val value: String = "123456",
) {
    fun build(): RawVerificationCode = RawVerificationCode(value = value)
}

fun generateVerificationCode(emailAddress: EmailAddress = EmailAddressBuilder().build()): VerificationCode =
    VerificationCode.generate(emailAddress)

fun generateRawVerificationCodeExcluding(verificationCode: VerificationCode): RawVerificationCode =
    RawVerificationCode(
        generateSequence { (0..<1_000_000).random() }
            .first { it != verificationCode.code.value.toInt() }
            .toString()
            .padStart(6, '0'),
    )
