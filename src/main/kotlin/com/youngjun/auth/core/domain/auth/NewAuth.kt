package com.youngjun.auth.core.domain.auth

import org.springframework.security.crypto.password.PasswordEncoder

data class NewAuth(
    val username: String,
    val password: String,
) {
    fun encodedWith(passwordEncoder: PasswordEncoder): NewAuth = NewAuth(username, passwordEncoder.encode(password))
}
