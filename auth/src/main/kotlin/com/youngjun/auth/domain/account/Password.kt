package com.youngjun.auth.domain.account

import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_BAD_CREDENTIALS
import org.springframework.security.crypto.password.PasswordEncoder

@JvmInline
value class Password private constructor(
    val value: String,
) {
    fun verify(
        rawPassword: RawPassword,
        passwordEncoder: PasswordEncoder,
    ) {
        if (!passwordEncoder.matches(rawPassword.value, value)) {
            throw AuthException(ACCOUNT_BAD_CREDENTIALS)
        }
    }

    companion object {
        fun encodedWith(
            rawPassword: RawPassword,
            passwordEncoder: PasswordEncoder,
        ): Password = Password(passwordEncoder.encode(rawPassword.value))
    }
}
