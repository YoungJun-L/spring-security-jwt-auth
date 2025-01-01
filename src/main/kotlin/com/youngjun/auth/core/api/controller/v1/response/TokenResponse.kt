package com.youngjun.auth.core.api.controller.v1.response

import com.youngjun.auth.core.domain.token.TokenPair

data class TokenResponse(
    val accessToken: String,
    val accessTokenExpiresIn: Long,
    val refreshToken: String,
    val refreshTokenExpiresIn: Long,
) {
    companion object {
        fun from(tokenPair: TokenPair): TokenResponse =
            TokenResponse(
                tokenPair.accessToken,
                tokenPair.accessTokenExpiresIn,
                tokenPair.refreshToken,
                tokenPair.refreshTokenExpiresIn,
            )
    }
}
