package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.domain.account.Account
import com.youngjun.auth.core.domain.account.AccountReader
import com.youngjun.auth.core.domain.token.RefreshToken
import com.youngjun.auth.core.domain.token.TokenPair
import com.youngjun.auth.core.domain.token.TokenProvider
import com.youngjun.auth.core.domain.token.TokenReader
import com.youngjun.auth.core.domain.token.TokenWriter
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val tokenWriter: TokenWriter,
    private val tokenReader: TokenReader,
    private val tokenProvider: TokenProvider,
    private val accountReader: AccountReader,
) {
    fun issue(account: Account): TokenPair {
        val tokenPair = tokenProvider.generate(account)
        tokenWriter.replaceTo(tokenPair)
        return tokenPair
    }

    fun reissue(refreshToken: RefreshToken): TokenPair {
        tokenProvider.verify(refreshToken)
        val token = tokenReader.read(refreshToken)
        val account = accountReader.readEnabled(token.userId)
        val tokenPair = tokenProvider.generate(account)
        tokenWriter.replaceTo(tokenPair)
        return tokenPair
    }
}
