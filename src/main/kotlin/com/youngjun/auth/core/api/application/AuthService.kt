package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.domain.auth.Auth
import com.youngjun.auth.core.domain.auth.AuthWriter
import com.youngjun.auth.core.domain.auth.NewAuth
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authWriter: AuthWriter,
    private val passwordEncoder: PasswordEncoder,
) {
    fun register(newAuth: NewAuth): Auth = authWriter.write(newAuth.encodedWith(passwordEncoder))
}
