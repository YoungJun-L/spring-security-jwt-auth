package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.domain.user.NewUser
import com.youngjun.auth.core.domain.user.User
import com.youngjun.auth.core.domain.user.UserWriter
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userWriter: UserWriter,
    private val passwordEncoder: PasswordEncoder,
) {
    fun register(newUser: NewUser): User = userWriter.write(newUser.encodedWith(passwordEncoder))
}
