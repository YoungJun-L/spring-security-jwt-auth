package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_NOT_FOUND_ERROR
import com.youngjun.auth.storage.db.core.token.TokenRepository
import org.springframework.stereotype.Component

@Component
class TokenReader(
    private val tokenRepository: TokenRepository,
) {
    fun read(refreshToken: String): Token = tokenRepository.read(refreshToken) ?: throw AuthException(TOKEN_NOT_FOUND_ERROR)
}
