package com.youngjun.auth.domain.token

import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepository : JpaRepository<RefreshToken, Long> {
    fun deleteByUserId(userId: Long)

    fun findByUserId(userId: Long): RefreshToken?
}
