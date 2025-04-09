package com.youngjun.admin.domain.administrator

import org.springframework.security.crypto.password.PasswordEncoder

@JvmInline
value class Password private constructor(
    val value: String,
) {
    companion object {
        fun encodedWith(
            rawPassword: RawPassword,
            passwordEncoder: PasswordEncoder,
        ): Password = Password(passwordEncoder.encode(rawPassword.value))
    }
}
