package com.youngjun.auth.application

import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.domain.account.AccountReader
import com.youngjun.auth.domain.token.RawAccessToken
import com.youngjun.auth.domain.token.RawRefreshToken
import com.youngjun.auth.domain.token.RefreshTokenStore
import com.youngjun.auth.domain.token.TokenPair
import com.youngjun.auth.domain.token.TokenPairGenerator
import com.youngjun.auth.domain.token.TokenParser
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val tokenPairGenerator: TokenPairGenerator,
    private val tokenParser: TokenParser,
    private val refreshTokenStore: RefreshTokenStore,
    private val accountReader: AccountReader,
) {
    fun issue(userId: Long): TokenPair = tokenPairGenerator.generate(userId).also { refreshTokenStore.replaceIfNotEmpty(it.refreshToken) }

    fun reissue(rawRefreshToken: RawRefreshToken): TokenPair {
        val parsedRefreshToken = tokenParser.parse(rawRefreshToken)
        accountReader.read(parsedRefreshToken.userId).verify()
        return tokenPairGenerator.generateOnExpiration(parsedRefreshToken).also { refreshTokenStore.replaceIfNotEmpty(it.refreshToken) }
    }

    fun parse(rawAccessToken: RawAccessToken): Account = accountReader.read(tokenParser.parse(rawAccessToken).userId).also { it.verify() }
}
