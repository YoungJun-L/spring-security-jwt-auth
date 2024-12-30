package com.youngjun.auth.api.controller.v1.request

import com.youngjun.auth.core.domain.token.RefreshToken

data class ReissueTokenRequest(
    val refreshToken: String,
) {
    fun toRefreshToken(): RefreshToken {
        require(refreshToken.isNotBlank()) { "Refresh Token validation error" }
        return RefreshToken(refreshToken)
    }
}
