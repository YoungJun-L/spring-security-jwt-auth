package com.youngjun.auth.storage.db.core.token

import org.springframework.data.jpa.repository.JpaRepository

interface TokenJpaRepository : JpaRepository<TokenEntity, Long> {
    fun deleteByUserId(userId: Long)

    fun findByUserId(userId: Long): TokenEntity?

    fun findByRefreshToken(refreshToken: String): TokenEntity?
}
