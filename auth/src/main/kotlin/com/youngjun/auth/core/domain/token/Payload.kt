package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.domain.support.EPOCH
import com.youngjun.auth.core.domain.support.toLocalDateTime
import io.jsonwebtoken.Claims
import java.time.LocalDateTime

data class Payload(
    val userId: Long,
    val expiration: LocalDateTime,
) {
    companion object {
        val Empty: Payload = Payload(0, EPOCH)

        fun from(claims: Claims): Payload = Payload(claims.subject.toLong(), claims.expiration.toLocalDateTime())
    }
}
