package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.infra.db.RefreshTokenJpaRepository
import org.springframework.stereotype.Repository

@Repository
class RefreshTokenRepository(
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
) {
    fun save(parsedRefreshToken: ParsedRefreshToken): RefreshToken =
        refreshTokenJpaRepository.save(RefreshToken(parsedRefreshToken.userId, parsedRefreshToken.value))

    fun deleteBy(userId: Long) = refreshTokenJpaRepository.deleteByUserId(userId)

    fun findBy(parsedRefreshToken: ParsedRefreshToken): RefreshToken? =
        refreshTokenJpaRepository.findByUserId(parsedRefreshToken.userId)?.takeIf { it.value == parsedRefreshToken.value }

    fun findBy(account: Account): RefreshToken? = refreshTokenJpaRepository.findByUserId(account.id)
}
