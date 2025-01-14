package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.domain.token.RefreshToken
import com.youngjun.auth.core.domain.token.TokenPair
import com.youngjun.auth.core.domain.token.TokenPairGenerator
import com.youngjun.auth.core.domain.token.TokenParser
import com.youngjun.auth.core.domain.token.TokenReader
import com.youngjun.auth.core.domain.token.TokenWriter
import com.youngjun.auth.core.domain.user.User
import com.youngjun.auth.core.domain.user.UserReader
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val tokenPairGenerator: TokenPairGenerator,
    private val tokenWriter: TokenWriter,
    private val tokenReader: TokenReader,
    private val tokenParser: TokenParser,
    private val userReader: UserReader,
) {
    fun issue(user: User): TokenPair {
        val tokenPair = tokenPairGenerator.issue(user)
        tokenWriter.replaceTo(tokenPair)
        return tokenPair
    }

    fun reissue(refreshToken: RefreshToken): TokenPair {
        tokenParser.verify(refreshToken)
        val token = tokenReader.read(refreshToken)
        val user = userReader.readEnabled(token.userId)
        val tokenPair = tokenPairGenerator.issue(user)
        tokenWriter.replaceTo(tokenPair)
        return tokenPair
    }
}
