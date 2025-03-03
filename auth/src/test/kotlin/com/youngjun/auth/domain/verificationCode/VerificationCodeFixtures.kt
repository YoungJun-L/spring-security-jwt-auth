package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EMAIL_ADDRESS
import com.youngjun.auth.domain.account.EmailAddress

fun generateVerificationCode(emailAddress: EmailAddress = EMAIL_ADDRESS): VerificationCode = VerificationCode.generate(emailAddress)

fun generateRawVerificationCodeExcluding(verificationCode: VerificationCode): RawVerificationCode =
    RawVerificationCode(
        generateSequence { (0..<1_000_000).random() }
            .first { it != verificationCode.code.value.toInt() }
            .toString()
            .padStart(6, '0'),
    )

val RAW_VERIFICATION_CODE: RawVerificationCode = RawVerificationCode("012345")
