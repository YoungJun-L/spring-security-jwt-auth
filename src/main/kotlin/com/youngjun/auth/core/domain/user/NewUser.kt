package com.youngjun.auth.core.domain.user

import org.springframework.security.crypto.password.PasswordEncoder

data class NewUser(
    val username: String,
    val password: String,
) {
    fun encodedWith(passwordEncoder: PasswordEncoder): NewUser = NewUser(username, passwordEncoder.encode(password))
}
