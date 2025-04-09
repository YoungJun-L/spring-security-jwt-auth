package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.account.Account
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class RefreshTokenStore(
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    @Transactional
    fun replace(parsedRefreshToken: ParsedRefreshToken) {
        refreshTokenRepository.deleteByUserId(parsedRefreshToken.userId)
        refreshTokenRepository.save(RefreshToken(parsedRefreshToken.userId, parsedRefreshToken.rawToken))
    }

    @Transactional
    fun expireIfExists(account: Account) {
        refreshTokenRepository.findByUserId(account.id)?.expire()
    }
}
