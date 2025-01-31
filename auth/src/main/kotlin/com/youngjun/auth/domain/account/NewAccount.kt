package com.youngjun.auth.domain.account

import org.springframework.security.crypto.password.PasswordEncoder

data class NewAccount(
    val username: String,
    val password: String,
) {
    fun encodedWith(passwordEncoder: PasswordEncoder): NewAccount = NewAccount(username, passwordEncoder.encode(password))
}
