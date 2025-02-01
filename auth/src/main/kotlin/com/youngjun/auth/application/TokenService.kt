package com.youngjun.auth.application

import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.domain.account.AccountReader
import com.youngjun.auth.domain.token.RawAccessToken
import com.youngjun.auth.domain.token.RawRefreshToken
import com.youngjun.auth.domain.token.RefreshTokenWriter
import com.youngjun.auth.domain.token.TokenPair
import com.youngjun.auth.domain.token.TokenPairGenerator
import com.youngjun.auth.domain.token.TokenParser
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val tokenPairGenerator: TokenPairGenerator,
    private val tokenParser: TokenParser,
    private val refreshTokenWriter: RefreshTokenWriter,
    private val accountReader: AccountReader,
) {
    fun issue(userId: Long): TokenPair = tokenPairGenerator.generate(userId).also { refreshTokenWriter.replace(it.refreshToken) }

    fun reissue(rawRefreshToken: RawRefreshToken): TokenPair {
        val parsedRefreshToken = tokenParser.parse(rawRefreshToken)
        accountReader.readEnabled(parsedRefreshToken.userId)
        return tokenPairGenerator.generateOnExpiration(parsedRefreshToken).also { refreshTokenWriter.update(it.refreshToken) }
    }

    fun parse(rawAccessToken: RawAccessToken): Account = accountReader.readEnabled(tokenParser.parse(rawAccessToken).userId)
}
