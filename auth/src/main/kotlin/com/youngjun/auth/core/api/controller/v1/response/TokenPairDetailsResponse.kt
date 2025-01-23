package com.youngjun.auth.core.api.controller.v1.response

import com.youngjun.auth.core.domain.support.toEpochSecond
import com.youngjun.auth.core.domain.token.TokenPairDetails

data class TokenPairDetailsResponse(
    val accessToken: String,
    val accessTokenExpiration: Long,
    val refreshToken: String,
    val refreshTokenExpiration: Long,
) {
    companion object {
        fun from(tokenPairDetails: TokenPairDetails): TokenPairDetailsResponse =
            TokenPairDetailsResponse(
                tokenPairDetails.accessToken.value,
                tokenPairDetails.accessTokenExpiration.toEpochSecond(),
                tokenPairDetails.refreshToken.value,
                tokenPairDetails.refreshTokenExpiration.toEpochSecond(),
            )
    }
}
