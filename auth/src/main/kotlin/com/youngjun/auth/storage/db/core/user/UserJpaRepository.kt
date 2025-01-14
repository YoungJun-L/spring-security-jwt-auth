package com.youngjun.auth.storage.db.core.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun findByUsername(username: String): UserEntity?

    fun existsByUsername(username: String): Boolean
}
