package com.youngjun.auth.domain.account

import org.springframework.security.crypto.password.PasswordEncoder

data class RawPassword(
    val value: String,
) {
    init {
        require(value.length in 8..<65) { "Password validation error" }
    }

    fun encodeWith(passwordEncoder: PasswordEncoder): Password = Password(passwordEncoder.encode(value))
}
