package com.youngjun.auth.core.domain.user

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.USER_DUPLICATE_ERROR
import com.youngjun.auth.storage.db.core.user.UserRepository
import org.springframework.stereotype.Component

@Component
class UserWriter(
    private val userRepository: UserRepository,
) {
    fun write(newUser: NewUser): User {
        if (userRepository.existsByUsername(newUser.username)) {
            throw AuthException(USER_DUPLICATE_ERROR)
        }
        return userRepository.write(newUser)
    }
}
