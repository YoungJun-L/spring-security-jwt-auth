package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.domain.account.Account
import com.youngjun.auth.core.domain.account.AccountReader
import com.youngjun.auth.core.domain.token.RawAccessToken
import com.youngjun.auth.core.domain.token.RawRefreshToken
import com.youngjun.auth.core.domain.token.TokenPair
import com.youngjun.auth.core.domain.token.TokenPairGenerator
import com.youngjun.auth.core.domain.token.TokenParser
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val tokenPairGenerator: TokenPairGenerator,
    private val tokenParser: TokenParser,
    private val accountReader: AccountReader,
) {
    fun issue(userId: Long): TokenPair = tokenPairGenerator.issue(userId)

    fun reissue(rawRefreshToken: RawRefreshToken): TokenPair {
        val parsedRefreshToken = tokenParser.parse(rawRefreshToken)
        accountReader.readEnabled(parsedRefreshToken.userId)
        return tokenPairGenerator.reissue(parsedRefreshToken)
    }

    fun parse(rawAccessToken: RawAccessToken): Account = accountReader.readEnabled(tokenParser.parse(rawAccessToken).userId)
}
