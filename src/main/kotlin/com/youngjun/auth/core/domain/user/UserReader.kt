package com.youngjun.auth.core.domain.user

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.UNAUTHORIZED_ERROR
import com.youngjun.auth.core.api.support.error.ErrorType.USER_NOT_FOUND_ERROR
import com.youngjun.auth.storage.db.core.user.UserRepository
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Component

@Component
class UserReader(
    private val userRepository: UserRepository,
) {
    fun read(username: String): User = userRepository.read(username) ?: throw UsernameNotFoundException(USER_NOT_FOUND_ERROR.message)

    fun readEnabled(id: Long): User {
        val user = userRepository.read(id) ?: throw AuthException(UNAUTHORIZED_ERROR)
        user.verify()
        return user
    }
}
