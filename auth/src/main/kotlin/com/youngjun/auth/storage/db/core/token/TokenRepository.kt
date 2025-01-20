package com.youngjun.auth.storage.db.core.token

import com.youngjun.auth.core.domain.token.NewToken
import com.youngjun.auth.core.domain.token.RefreshToken
import com.youngjun.auth.core.domain.token.Token
import jakarta.transaction.Transactional
import org.springframework.stereotype.Repository

@Repository
class TokenRepository(
    private val tokenJpaRepository: TokenJpaRepository,
) {
    @Transactional
    fun replace(newToken: NewToken): Token {
        tokenJpaRepository.deleteByUserId(newToken.userId)
        return tokenJpaRepository.save(TokenEntity(newToken.userId, newToken.refreshToken)).toToken()
    }

    @Transactional
    fun update(newToken: NewToken): Token {
        val tokenEntity = tokenJpaRepository.findByUserId(newToken.userId)!!
        tokenEntity.refreshToken = newToken.refreshToken
        return tokenEntity.toToken()
    }

    fun read(refreshToken: RefreshToken): Token? = tokenJpaRepository.findByRefreshToken(refreshToken.value)?.toToken()
}
