package com.youngjun.auth.core.domain.auth

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.AUTH_NOT_FOUND_ERROR
import com.youngjun.auth.core.api.support.error.ErrorType.UNAUTHORIZED_ERROR
import com.youngjun.auth.storage.db.core.auth.AuthRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class AuthReader(
    private val authRepository: AuthRepository,
) {
    fun read(username: String): Auth = authRepository.read(username) ?: throw UsernameNotFoundException(AUTH_NOT_FOUND_ERROR.message)

    fun readEnabled(id: Long): Auth {
        val auth = authRepository.read(id) ?: throw AuthException(UNAUTHORIZED_ERROR)
        auth.verify()
        return auth
    }
}
