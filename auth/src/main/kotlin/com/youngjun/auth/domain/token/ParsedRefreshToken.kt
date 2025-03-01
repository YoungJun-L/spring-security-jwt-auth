package com.youngjun.auth.domain.token

import java.time.Duration
import java.time.LocalDateTime

data class ParsedRefreshToken(
    val rawToken: RawRefreshToken,
    private val payload: Payload,
) {
    val userId: Long get() = payload.userId
    val expiration: LocalDateTime get() = payload.expiration

    fun isNotEmpty(): Boolean = this != Empty

    fun isExpiringSoon(
        now: LocalDateTime = LocalDateTime.now(),
        threshold: Duration,
    ): Boolean = now in (expiration - threshold..<expiration)

    companion object {
        val Empty: ParsedRefreshToken = ParsedRefreshToken(RawRefreshToken(""), Payload.Empty)
    }
}
