package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.domain.account.Account
import com.youngjun.auth.storage.db.core.token.RefreshTokenRepository
import org.springframework.stereotype.Component

@Component
class TokenPairGenerator(
    private val jwtGenerator: JwtGenerator,
    private val refreshTokenRepository: RefreshTokenRepository,
) {
    fun issue(account: Account): TokenPair =
        TokenPair(
            account.id,
            jwtGenerator.generateAccessToken(account),
            jwtGenerator.generateRefreshToken(account),
        ).also {
            refreshTokenRepository.replace(it.refreshToken)
        }

    fun reissue(
        account: Account,
        parsedRefreshToken: ParsedRefreshToken,
    ): TokenPair =
        TokenPair(
            account.id,
            jwtGenerator.generateAccessToken(account),
            jwtGenerator.reissueRefreshToken(account, parsedRefreshToken.expiration),
        ).also {
            if (it.refreshToken.exists()) {
                refreshTokenRepository.update(it.refreshToken)
            }
        }
}
