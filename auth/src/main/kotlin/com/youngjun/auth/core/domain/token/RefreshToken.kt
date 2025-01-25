package com.youngjun.auth.core.domain.token

data class RefreshToken(
    val value: String,
) {
    companion object {
        val Empty: RefreshToken = RefreshToken("")
    }
}
