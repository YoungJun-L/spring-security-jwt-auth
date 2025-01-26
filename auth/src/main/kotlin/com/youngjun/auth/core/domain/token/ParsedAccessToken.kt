package com.youngjun.auth.core.domain.token

import java.time.LocalDateTime

data class ParsedAccessToken(
    val value: String,
    private val payload: Payload,
) {
    val userId: Long get() = payload.userId
    val expiration: LocalDateTime get() = payload.expiration

    companion object {
        fun of(
            rawAccessToken: RawAccessToken,
            payload: Payload,
        ): ParsedAccessToken = ParsedAccessToken(rawAccessToken.value, payload)
    }
}
