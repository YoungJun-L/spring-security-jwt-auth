package com.youngjun.auth.api.controller.v1.response

import com.youngjun.auth.domain.support.toEpochSecond
import com.youngjun.auth.domain.token.RawAccessToken
import com.youngjun.auth.domain.token.RawRefreshToken
import com.youngjun.auth.domain.token.TokenPair

data class TokenPairResponse(
    val accessToken: RawAccessToken,
    val accessTokenExpiration: Long,
    val refreshToken: RawRefreshToken,
    val refreshTokenExpiration: Long,
) {
    companion object {
        fun from(tokenPair: TokenPair): TokenPairResponse =
            TokenPairResponse(
                tokenPair.accessToken.value,
                tokenPair.accessToken.expiration.toEpochSecond(),
                tokenPair.refreshToken.value,
                tokenPair.refreshToken.expiration.toEpochSecond(),
            )
    }
}
