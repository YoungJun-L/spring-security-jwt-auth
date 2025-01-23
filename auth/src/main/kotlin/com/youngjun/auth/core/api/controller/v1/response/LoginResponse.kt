package com.youngjun.auth.core.api.controller.v1.response

import com.youngjun.auth.core.domain.token.TokenPairDetails

data class LoginResponse(
    val userId: Long,
    val tokens: TokenPairDetailsResponse,
) {
    companion object {
        fun from(tokenPairDetails: TokenPairDetails): LoginResponse =
            LoginResponse(tokenPairDetails.userId, TokenPairDetailsResponse.from(tokenPairDetails))
    }
}
