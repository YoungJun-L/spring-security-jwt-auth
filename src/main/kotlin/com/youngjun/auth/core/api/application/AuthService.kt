package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.domain.auth.Auth
import com.youngjun.auth.core.domain.auth.AuthReader
import com.youngjun.auth.core.domain.auth.AuthWriter
import com.youngjun.auth.core.domain.auth.NewAuth
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val authReader: AuthReader,
    private val authWriter: AuthWriter,
    private val passwordEncoder: PasswordEncoder,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): Auth = authReader.read(username)

    fun register(newAuth: NewAuth): Auth {
        val encodedPassword = passwordEncoder.encode(newAuth.password)
        return authWriter.write(newAuth.encoded(encodedPassword))
    }
}
