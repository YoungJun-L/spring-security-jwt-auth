package com.youngjun.auth.domain.token

data class RawAccessToken(
    val value: String,
) {
    fun parsed(payload: Payload): ParsedAccessToken = ParsedAccessToken(value, payload)
}
