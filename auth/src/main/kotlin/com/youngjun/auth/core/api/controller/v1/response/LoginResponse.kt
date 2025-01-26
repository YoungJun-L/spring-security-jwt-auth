package com.youngjun.auth.core.api.controller.v1.response

import com.youngjun.auth.core.domain.token.TokenPair

data class LoginResponse(
    val userId: Long,
    val tokens: TokenPairResponse,
) {
    companion object {
        fun from(tokenPair: TokenPair): LoginResponse = LoginResponse(tokenPair.userId, TokenPairResponse.from(tokenPair))
    }
}
