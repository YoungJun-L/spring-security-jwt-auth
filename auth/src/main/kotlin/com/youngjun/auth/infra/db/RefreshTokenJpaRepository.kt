package com.youngjun.auth.infra.db

import com.youngjun.auth.domain.token.RefreshToken
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenJpaRepository : JpaRepository<RefreshToken, Long> {
    fun deleteByUserId(userId: Long)

    fun findByUserId(userId: Long): RefreshToken?
}
