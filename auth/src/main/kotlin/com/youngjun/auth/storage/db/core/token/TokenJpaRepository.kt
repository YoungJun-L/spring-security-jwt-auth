package com.youngjun.auth.storage.db.core.token

import org.springframework.data.jpa.repository.JpaRepository

interface TokenJpaRepository : JpaRepository<TokenEntity, Long> {
    fun findByRefreshToken(refreshToken: String): List<TokenEntity>

    fun deleteByUserId(userId: Long)
}
