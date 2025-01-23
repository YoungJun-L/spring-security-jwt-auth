package com.youngjun.auth.core.api.controller.v1.request

import com.youngjun.auth.core.domain.token.RefreshToken

data class ReissueTokenRequest(
    val refreshToken: String,
) {
    fun toRefreshToken(): RefreshToken = RefreshToken(refreshToken)
}
