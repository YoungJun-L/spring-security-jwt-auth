package com.youngjun.auth.domain.token

import java.time.LocalDateTime

data class ParsedAccessToken(
    val value: RawAccessToken,
    private val payload: Payload,
) {
    val userId: Long get() = payload.userId
    val expiration: LocalDateTime get() = payload.expiration
}
