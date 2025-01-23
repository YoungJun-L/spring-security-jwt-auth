package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_NOT_FOUND_ERROR
import com.youngjun.auth.storage.db.core.token.RefreshTokenRepository
import org.springframework.stereotype.Component

@Component
class RefreshTokenReader(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val tokenParser: TokenParser,
) {
    fun readEnabled(refreshToken: RefreshToken): RefreshTokenDetails {
        val userId = tokenParser.parseUserId(refreshToken)
        val refreshTokenDetails =
            refreshTokenRepository.read(userId) ?: throw AuthException(TOKEN_NOT_FOUND_ERROR)
        refreshTokenDetails.verify()
        return refreshTokenDetails
    }
}
