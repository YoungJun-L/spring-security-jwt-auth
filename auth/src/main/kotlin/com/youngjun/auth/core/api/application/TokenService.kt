package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.domain.account.Account
import com.youngjun.auth.core.domain.account.AccountReader
import com.youngjun.auth.core.domain.token.RefreshToken
import com.youngjun.auth.core.domain.token.TokenPair
import com.youngjun.auth.core.domain.token.TokenPairGenerator
import com.youngjun.auth.core.domain.token.TokenParser
import com.youngjun.auth.core.domain.token.TokenReader
import com.youngjun.auth.core.domain.token.TokenWriter
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val tokenPairGenerator: TokenPairGenerator,
    private val tokenWriter: TokenWriter,
    private val tokenReader: TokenReader,
    private val tokenParser: TokenParser,
    private val accountReader: AccountReader,
) {
    fun issue(account: Account): TokenPair {
        val tokenPair = tokenPairGenerator.generate(account)
        tokenWriter.replaceTo(tokenPair)
        return tokenPair
    }

    fun reissue(refreshToken: RefreshToken): TokenPair {
        tokenParser.verify(refreshToken)
        val token = tokenReader.read(refreshToken)
        val account = accountReader.readEnabled(token.userId)
        val tokenPair = tokenPairGenerator.generate(account)
        tokenWriter.replaceTo(tokenPair)
        return tokenPair
    }
}
