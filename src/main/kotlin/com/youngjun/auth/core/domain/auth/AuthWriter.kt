package com.youngjun.auth.core.domain.auth

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType
import com.youngjun.auth.storage.db.core.auth.AuthRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class AuthWriter(
    private val authRepository: AuthRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun write(newAuth: NewAuth): Auth {
        if (authRepository.existsByUsername(newAuth.username)) {
            throw AuthException(ErrorType.AUTH_DUPLICATE_ERROR, null)
        }
        val encodedPassword = passwordEncoder.encode(newAuth.password)
        return authRepository.write(newAuth.encoded(encodedPassword))
    }
}
