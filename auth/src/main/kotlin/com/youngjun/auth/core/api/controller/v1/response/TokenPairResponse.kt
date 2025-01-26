package com.youngjun.auth.core.api.controller.v1.response

import com.youngjun.auth.core.domain.support.toEpochSecond
import com.youngjun.auth.core.domain.token.TokenPair

data class TokenPairResponse(
    val accessToken: String,
    val accessTokenExpiration: Long,
    val refreshToken: String,
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
