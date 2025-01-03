package com.youngjun.auth.storage.db.core.auth

import org.springframework.data.jpa.repository.JpaRepository

interface AuthJpaRepository : JpaRepository<AuthEntity, Long> {
    fun findByUsername(username: String): AuthEntity?

    fun existsByUsername(username: String): Boolean
}
