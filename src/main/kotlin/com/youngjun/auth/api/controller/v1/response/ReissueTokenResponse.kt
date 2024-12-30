package com.youngjun.auth.api.controller.v1.response

import com.youngjun.auth.core.domain.token.TokenPair

data class ReissueTokenResponse(
    val accessToken: String,
    val accessTokenExpiresIn: Long,
    val refreshToken: String,
    val refreshTokenExpiresIn: Long,
) {
    companion object {
        fun from(tokenPair: TokenPair): ReissueTokenResponse =
            ReissueTokenResponse(
                tokenPair.accessToken,
                tokenPair.accessTokenExpiresIn,
                tokenPair.refreshToken,
                tokenPair.refreshTokenExpiresIn,
            )
    }
}
