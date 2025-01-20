package com.youngjun.auth.core.domain.token

import com.youngjun.auth.storage.db.core.token.TokenRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component

@Component
class TokenWriter(
    private val tokenRepository: TokenRepository,
) {
    @Transactional
    fun update(tokenPair: TokenPair): Token {
        tokenRepository.delete(tokenPair.userId)
        return tokenRepository.update(tokenPair)
    }
}
