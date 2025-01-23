package com.youngjun.auth.storage.db.core.token

import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenEntity, Long> {
    fun deleteByUserId(userId: Long)

    fun findByUserId(userId: Long): RefreshTokenEntity?
}
