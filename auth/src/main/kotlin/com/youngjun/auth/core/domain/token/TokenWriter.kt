package com.youngjun.auth.core.domain.token

import com.youngjun.auth.storage.db.core.token.TokenRepository
import org.springframework.stereotype.Component

@Component
class TokenWriter(
    private val tokenRepository: TokenRepository,
) {
    fun replace(newToken: NewToken): Token = tokenRepository.replace(newToken)

    fun update(newToken: NewToken): Token = tokenRepository.update(newToken)
}
