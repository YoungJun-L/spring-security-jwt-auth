package com.youngjun.auth.core.api.controller.v1.response

import com.youngjun.auth.core.domain.support.toEpochSecond
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
                tokenPair.accessTokenExpiration.toEpochSecond(),
                tokenPair.refreshToken,
                tokenPair.refreshTokenExpiration.toEpochSecond(),
            )
    }
}
