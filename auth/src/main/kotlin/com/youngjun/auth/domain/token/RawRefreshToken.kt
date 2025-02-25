package com.youngjun.auth.domain.token

data class RawRefreshToken(
    val value: String,
) {
    fun parsed(payload: Payload): ParsedRefreshToken = ParsedRefreshToken(value, payload)
}
