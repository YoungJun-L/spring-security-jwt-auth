package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.domain.account.Account
import com.youngjun.auth.core.domain.account.AccountReader
import com.youngjun.auth.core.domain.token.AccessToken
import com.youngjun.auth.core.domain.token.NewRefreshToken
import com.youngjun.auth.core.domain.token.RefreshToken
import com.youngjun.auth.core.domain.token.RefreshTokenReader
import com.youngjun.auth.core.domain.token.RefreshTokenWriter
import com.youngjun.auth.core.domain.token.TokenGenerator
import com.youngjun.auth.core.domain.token.TokenPairDetails
import com.youngjun.auth.core.domain.token.TokenParser
import org.springframework.stereotype.Service

@Service
class TokenService(
    private val refreshTokenWriter: RefreshTokenWriter,
    private val refreshTokenReader: RefreshTokenReader,
    private val tokenGenerator: TokenGenerator,
    private val tokenParser: TokenParser,
    private val accountReader: AccountReader,
) {
    fun issue(account: Account): TokenPairDetails {
        val tokenPairDetails = tokenGenerator.generate(account)
        refreshTokenWriter.replace(NewRefreshToken.from(tokenPairDetails))
        return tokenPairDetails
    }

    fun reissue(refreshToken: RefreshToken): TokenPairDetails {
        val refreshTokenDetails = refreshTokenReader.readEnabled(refreshToken)
        val account = accountReader.readEnabled(refreshTokenDetails.userId)
        val tokenPairDetails = tokenGenerator.generate(account)
        refreshTokenWriter.update(NewRefreshToken.from(tokenPairDetails))
        return tokenPairDetails
    }

    fun parse(accessToken: AccessToken): Account {
        val userId = tokenParser.parseUserId(accessToken.token)
        return accountReader.readEnabled(userId)
    }
}
