package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.infra.db.RefreshTokenJpaRepository
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
        val refreshToken = refreshTokenJpaRepository.findByUserId(parsedRefreshToken.userId)!!
        refreshToken.updateValue(parsedRefreshToken.value)
    }

    fun read(parsedRefreshToken: ParsedRefreshToken) =
        refreshTokenJpaRepository
            .findByUserId(parsedRefreshToken.userId)
            ?.takeIf { it.token == parsedRefreshToken.value }
            ?.toRefreshToken()

    fun read(account: Account) = refreshTokenJpaRepository.findByUserId(account.id)
}
