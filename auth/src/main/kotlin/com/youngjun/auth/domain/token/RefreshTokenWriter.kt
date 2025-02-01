package com.youngjun.auth.domain.token

import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class RefreshTokenWriter(
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    @Transactional
    fun replace(parsedRefreshToken: ParsedRefreshToken): RefreshToken {
        refreshTokenRepository.deleteBy(parsedRefreshToken.userId)
        return refreshTokenRepository.save(parsedRefreshToken)
    }

    @Transactional
    fun update(parsedRefreshToken: ParsedRefreshToken) {
        if (!parsedRefreshToken.exists()) {
            return
        }
        refreshTokenRepository.findBy(parsedRefreshToken.userId)?.updateValue(parsedRefreshToken.value)
    }
}
