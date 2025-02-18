package com.youngjun.auth.domain.account

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import org.springframework.security.crypto.password.PasswordEncoder

@Embeddable
data class Password private constructor(
    @Column(name = "password")
    val value: String,
) {
    companion object {
        fun encodedWith(
            rawPassword: RawPassword,
            passwordEncoder: PasswordEncoder,
        ): Password = Password(passwordEncoder.encode(rawPassword.value))
    }
}
