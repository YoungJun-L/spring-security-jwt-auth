package com.youngjun.auth.storage.db.core.token

import com.youngjun.auth.core.domain.token.ParsedRefreshToken
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
class RefreshTokenRepository(
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
) {
    @Transactional
    fun replace(parsedRefreshToken: ParsedRefreshToken) {
        refreshTokenJpaRepository.deleteByUserId(parsedRefreshToken.userId)
        refreshTokenJpaRepository.save(RefreshTokenEntity(parsedRefreshToken.userId, parsedRefreshToken.value))
    }

    @Transactional
    fun update(parsedRefreshToken: ParsedRefreshToken) {
        val refreshTokenEntity = refreshTokenJpaRepository.findByUserId(parsedRefreshToken.userId)!!
        refreshTokenEntity.token = parsedRefreshToken.value
    }

    fun read(parsedRefreshToken: ParsedRefreshToken) =
        refreshTokenJpaRepository
            .findByUserId(parsedRefreshToken.userId)
            ?.takeIf { it.token == parsedRefreshToken.value }
            ?.toRefreshToken()
}
