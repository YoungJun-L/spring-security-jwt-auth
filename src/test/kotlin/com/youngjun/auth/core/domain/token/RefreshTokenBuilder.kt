package com.youngjun.auth.core.domain.token

data class RefreshTokenBuilder(
    val value: String = "",
) {
    fun build(): RefreshToken =
        RefreshToken(
            value = value,
        )
}
