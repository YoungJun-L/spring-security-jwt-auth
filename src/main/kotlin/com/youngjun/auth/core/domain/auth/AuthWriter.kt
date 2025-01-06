package com.youngjun.auth.core.domain.auth

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType
import com.youngjun.auth.storage.db.core.auth.AuthRepository
import org.springframework.stereotype.Component

@Component
class AuthWriter(
    private val authRepository: AuthRepository,
) {
    fun write(newAuth: NewAuth): Auth {
        if (authRepository.existsByUsername(newAuth.username)) {
            throw AuthException(ErrorType.AUTH_DUPLICATE_ERROR)
        }
        return authRepository.write(newAuth)
    }
}
