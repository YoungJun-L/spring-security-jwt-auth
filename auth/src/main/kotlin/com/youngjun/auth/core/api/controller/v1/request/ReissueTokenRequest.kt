package com.youngjun.auth.core.api.controller.v1.request

import com.youngjun.auth.core.domain.token.RawRefreshToken

data class ReissueTokenRequest(
    val refreshToken: String,
) {
    fun toRawRefreshToken(): RawRefreshToken = RawRefreshToken(refreshToken)
}
