package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_NOT_FOUND_ERROR
import com.youngjun.auth.storage.db.core.token.RefreshTokenRepository
import org.springframework.stereotype.Component

@Component
class RefreshTokenReader(
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun read(refreshToken: RefreshToken): RefreshTokenDetails =
        refreshTokenRepository.read(refreshToken) ?: throw AuthException(TOKEN_NOT_FOUND_ERROR)
}
