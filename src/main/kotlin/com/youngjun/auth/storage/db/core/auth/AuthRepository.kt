package com.youngjun.auth.storage.db.core.auth

import com.youngjun.auth.core.domain.auth.Auth
import com.youngjun.auth.core.domain.auth.NewAuth
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class AuthRepository(
    private val authJpaRepository: AuthJpaRepository,
) {
    fun write(newAuth: NewAuth): Auth {
        val savedAuth = authJpaRepository.save(AuthEntity(newAuth.username, newAuth.password, newAuth.status))
        return savedAuth.toAuth()
    }

    fun read(username: String): Auth? = authJpaRepository.findByUsername(username)?.toAuth()

    fun read(id: Long): Auth? = authJpaRepository.findByIdOrNull(id)?.toAuth()

    fun existsByUsername(username: String): Boolean = authJpaRepository.existsByUsername(username)
}
