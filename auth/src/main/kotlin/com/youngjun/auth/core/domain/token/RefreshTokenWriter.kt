package com.youngjun.auth.core.domain.token

import com.youngjun.auth.storage.db.core.token.RefreshTokenRepository
import org.springframework.stereotype.Component

@Component
class RefreshTokenWriter(
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun replace(tokenPairDetails: TokenPairDetails): RefreshTokenDetails =
        refreshTokenRepository.replace(tokenPairDetails.userId, tokenPairDetails.refreshToken)

    fun update(tokenPairDetails: TokenPairDetails): RefreshTokenDetails =
        refreshTokenRepository.update(tokenPairDetails.userId, tokenPairDetails.refreshToken)
}
