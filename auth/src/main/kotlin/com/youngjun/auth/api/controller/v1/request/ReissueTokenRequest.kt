package com.youngjun.auth.api.controller.v1.request

import com.youngjun.auth.domain.token.RawRefreshToken

data class ReissueTokenRequest(
    val refreshToken: String,
) {
    fun toRawRefreshToken(): RawRefreshToken = RawRefreshToken(refreshToken)
}
