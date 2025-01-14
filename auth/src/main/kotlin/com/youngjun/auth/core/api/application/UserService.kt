package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.domain.user.NewUser
import com.youngjun.auth.core.domain.user.User
import com.youngjun.auth.core.domain.user.UserReader
import com.youngjun.auth.core.domain.user.UserWriter
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userReader: UserReader,
    private val userWriter: UserWriter,
    private val passwordEncoder: PasswordEncoder,
) : UserDetailsService {
    override fun loadUserByUsername(username: String): User = userReader.read(username)

    fun register(newUser: NewUser): User = userWriter.write(newUser.encodedWith(passwordEncoder))
}
