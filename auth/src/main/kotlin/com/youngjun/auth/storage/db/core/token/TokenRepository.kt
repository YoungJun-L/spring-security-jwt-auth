package com.youngjun.auth.storage.db.core.token

import com.youngjun.auth.core.domain.token.RefreshToken
import com.youngjun.auth.core.domain.token.Token
import com.youngjun.auth.core.domain.token.TokenPair
import org.springframework.stereotype.Repository

@Repository
class TokenRepository(
    private val tokenJpaRepository: TokenJpaRepository,
) {
    fun delete(userId: Long) {
        tokenJpaRepository.deleteByUserId(userId)
    }

    fun write(tokenPair: TokenPair): Token = tokenJpaRepository.save(TokenEntity(tokenPair.userId, tokenPair.refreshToken)).toToken()

    fun read(refreshToken: RefreshToken): List<Token> = tokenJpaRepository.findByRefreshToken(refreshToken.value).map { it.toToken() }
}
