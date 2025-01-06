package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.domain.auth.Auth
import com.youngjun.auth.core.domain.auth.AuthReader
import com.youngjun.auth.core.domain.token.RefreshToken
import com.youngjun.auth.core.domain.token.TokenPair
import com.youngjun.auth.core.domain.token.TokenPairGenerator
import com.youngjun.auth.core.domain.token.TokenReader
import com.youngjun.auth.core.domain.token.TokenWriter
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val tokenPairGenerator: TokenPairGenerator,
    private val tokenWriter: TokenWriter,
    private val tokenReader: TokenReader,
    private val authReader: AuthReader,
) {
    fun issue(auth: Auth): TokenPair {
        val tokenPair = tokenPairGenerator.issue(auth)
        tokenWriter.replaceTo(tokenPair)
        return tokenPair
    }

    fun reissue(refreshToken: RefreshToken): TokenPair {
        val token = tokenReader.readVerified(refreshToken)
        val auth = authReader.readEnabled(token.authId)
        val tokenPair = tokenPairGenerator.issue(auth)
        tokenWriter.replaceTo(tokenPair)
        return tokenPair
    }
}
