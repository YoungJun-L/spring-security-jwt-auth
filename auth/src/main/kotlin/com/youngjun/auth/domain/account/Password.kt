package com.youngjun.auth.domain.account

import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_UNCHANGED_PASSWORD
import org.springframework.security.crypto.password.PasswordEncoder

@JvmInline
value class Password private constructor(
    val value: String,
) {
    fun checkChanged(
        rawPassword: RawPassword,
        passwordEncoder: PasswordEncoder,
    ) {
        if (passwordEncoder.matches(rawPassword.value, value)) {
            throw AuthException(ACCOUNT_UNCHANGED_PASSWORD)
        }
    }

    companion object {
        fun encodedWith(
            rawPassword: RawPassword,
            passwordEncoder: PasswordEncoder,
        ): Password = Password(passwordEncoder.encode(rawPassword.value))
    }
}
