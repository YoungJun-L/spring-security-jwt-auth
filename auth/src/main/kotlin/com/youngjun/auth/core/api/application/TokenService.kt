package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.domain.account.Account
import com.youngjun.auth.core.domain.account.AccountReader
import com.youngjun.auth.core.domain.token.NewToken
import com.youngjun.auth.core.domain.token.TokenGenerator
import com.youngjun.auth.core.domain.token.TokenPair
import com.youngjun.auth.core.domain.token.TokenParser
import com.youngjun.auth.core.domain.token.TokenReader
import com.youngjun.auth.core.domain.token.TokenWriter
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val tokenWriter: TokenWriter,
    private val tokenReader: TokenReader,
    private val tokenGenerator: TokenGenerator,
    private val tokenParser: TokenParser,
    private val accountReader: AccountReader,
) {
    fun issue(account: Account): TokenPair {
        val tokenPair = tokenGenerator.generate(account)
        tokenWriter.replace(NewToken(account.id, tokenPair.refreshToken))
        return tokenPair
    }

    fun reissue(refreshToken: String): TokenPair {
        tokenParser.parseSubject(refreshToken)
        val token = tokenReader.read(refreshToken)
        val account = accountReader.readEnabled(token.userId)
        val tokenPair = tokenGenerator.generate(account)
        tokenWriter.update(NewToken.from(tokenPair))
        return tokenPair
    }
}
