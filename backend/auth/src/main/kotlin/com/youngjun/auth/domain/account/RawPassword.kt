package com.youngjun.auth.domain.account

import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_UNCHANGED_PASSWORD

@JvmInline
value class RawPassword(
    val value: String,
) {
    init {
        require(value.length in 8..<65) { "Password validation error" }
    }

    fun checkChanged(rawPassword: RawPassword) {
        if (this == rawPassword) {
            throw AuthException(ACCOUNT_UNCHANGED_PASSWORD)
        }
    }
}
