package com.youngjun.auth.core.domain.token

import com.youngjun.auth.storage.db.core.token.RefreshTokenRepository
import org.springframework.stereotype.Component

@Component
class RefreshTokenWriter(
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun replace(newRefreshToken: NewRefreshToken): RefreshTokenDetails = refreshTokenRepository.replace(newRefreshToken)

    fun update(newRefreshToken: NewRefreshToken): RefreshTokenDetails = refreshTokenRepository.update(newRefreshToken)
}
