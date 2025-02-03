package com.youngjun.auth.domain.token

import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class RefreshTokenWriter(
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    @Transactional
    fun replaceIfNotEmpty(parsedRefreshToken: ParsedRefreshToken) {
        if (!parsedRefreshToken.isNotEmpty()) {
            return
        }
        refreshTokenRepository.deleteBy(parsedRefreshToken.userId)
        refreshTokenRepository.save(parsedRefreshToken)
    }
}
