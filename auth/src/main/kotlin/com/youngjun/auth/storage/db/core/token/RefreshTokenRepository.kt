package com.youngjun.auth.storage.db.core.token

import com.youngjun.auth.core.domain.token.NewRefreshToken
import com.youngjun.auth.core.domain.token.RefreshToken
import com.youngjun.auth.core.domain.token.RefreshTokenDetails
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
class RefreshTokenRepository(
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
) {
    @Transactional
    fun replace(newRefreshToken: NewRefreshToken): RefreshTokenDetails {
        refreshTokenJpaRepository.deleteByUserId(newRefreshToken.userId)
        return refreshTokenJpaRepository
            .save(RefreshTokenEntity(newRefreshToken.userId, newRefreshToken.refreshToken.value))
            .toRefreshTokenDetails()
    }

    @Transactional
    fun update(newRefreshToken: NewRefreshToken): RefreshTokenDetails {
        val refreshTokenEntity = refreshTokenJpaRepository.findByUserId(newRefreshToken.userId)!!
        refreshTokenEntity.token = newRefreshToken.refreshToken.value
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
