package com.youngjun.auth.infra.db

import com.youngjun.auth.domain.token.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenEntity, Long> {
    fun deleteByUserId(userId: Long)

    fun findByUserId(userId: Long): RefreshTokenEntity?
}
