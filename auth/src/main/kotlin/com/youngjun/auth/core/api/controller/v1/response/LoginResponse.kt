package com.youngjun.auth.core.api.controller.v1.response

import com.youngjun.auth.core.domain.token.TokenPair

data class LoginResponse(
    val userId: Long,
    val tokens: TokenResponse,
) {
    companion object {
        fun from(tokenPair: TokenPair): LoginResponse = LoginResponse(tokenPair.userId, TokenResponse.from(tokenPair))
    }
}
