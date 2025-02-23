package com.youngjun.auth.security.handler

import com.youngjun.auth.api.controller.v1.response.TokenPairResponse
import com.youngjun.auth.domain.token.TokenPair

data class UserTokenResponse(
    val userId: Long,
    val tokens: TokenPairResponse,
) {
    companion object {
        fun from(tokenPair: TokenPair): UserTokenResponse = UserTokenResponse(tokenPair.userId, TokenPairResponse.from(tokenPair))
    }
}
