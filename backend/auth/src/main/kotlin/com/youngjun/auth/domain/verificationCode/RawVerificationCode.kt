package com.youngjun.auth.domain.verificationCode

@JvmInline
value class RawVerificationCode(
    val value: String,
) {
    init {
        require(value.length == 6 && value.all { it.isDigit() }) { "Verification code validation error" }
    }
}
