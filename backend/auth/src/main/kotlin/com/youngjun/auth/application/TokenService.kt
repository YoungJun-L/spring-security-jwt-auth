package com.youngjun.auth.application

import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.domain.account.AccountReader
import com.youngjun.auth.domain.token.RawAccessToken
import com.youngjun.auth.domain.token.RawRefreshToken
import com.youngjun.auth.domain.token.TokenPair
import com.youngjun.auth.domain.token.TokenPairGenerator
import com.youngjun.auth.domain.token.TokenParser
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class TokenService(
    private val tokenPairGenerator: TokenPairGenerator,
    private val tokenParser: TokenParser,
    private val accountReader: AccountReader,
) {
    fun issue(
        userId: Long,
        now: LocalDateTime = LocalDateTime.now(),
    ): TokenPair = tokenPairGenerator.generate(userId, now)

    fun reissue(
        rawRefreshToken: RawRefreshToken,
        now: LocalDateTime = LocalDateTime.now(),
    ): TokenPair {
        val parsedRefreshToken = tokenParser.parse(rawRefreshToken)
        accountReader.read(parsedRefreshToken.userId).verify()
        return tokenPairGenerator.generateOnExpiration(parsedRefreshToken, now)
    }

    fun parse(rawAccessToken: RawAccessToken): Account = accountReader.read(tokenParser.parse(rawAccessToken).userId).apply { verify() }
}
