package com.youngjun.auth.storage.db.core.token

import com.youngjun.auth.core.domain.token.RefreshToken
import com.youngjun.auth.core.domain.token.RefreshTokenDetails
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
class RefreshTokenRepository(
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
) {
    @Transactional
    fun replace(
        userId: Long,
        refreshToken: RefreshToken,
    ): RefreshTokenDetails {
        refreshTokenJpaRepository.deleteByUserId(userId)
        return refreshTokenJpaRepository.save(RefreshTokenEntity(userId, refreshToken.value)).toRefreshTokenDetails()
    }

    @Transactional
    fun update(
        userId: Long,
        refreshToken: RefreshToken,
    ): RefreshTokenDetails {
        val refreshTokenEntity = refreshTokenJpaRepository.findByUserId(userId)!!
        refreshTokenEntity.token = refreshToken.value
        return refreshTokenEntity.toRefreshTokenDetails()
    }

    fun read(
        userId: Long,
        refreshToken: RefreshToken,
    ): RefreshTokenDetails? =
        refreshTokenJpaRepository
            .findByUserId(userId)
            ?.takeIf { it.token == refreshToken.value }
            ?.toRefreshTokenDetails()
}
