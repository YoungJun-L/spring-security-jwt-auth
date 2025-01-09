package com.youngjun.auth.storage.db.core.user

import com.youngjun.auth.core.domain.user.NewUser
import com.youngjun.auth.core.domain.user.User
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class UserRepository(
    private val userJpaRepository: UserJpaRepository,
) {
    fun write(newUser: NewUser): User {
        val savedUser = userJpaRepository.save(UserEntity(newUser.username, newUser.password))
        return savedUser.toUser()
    }

    fun read(username: String): User? = userJpaRepository.findByUsername(username)?.toUser()

    fun read(id: Long): User? = userJpaRepository.findByIdOrNull(id)?.toUser()

    fun existsByUsername(username: String): Boolean = userJpaRepository.existsByUsername(username)
}
