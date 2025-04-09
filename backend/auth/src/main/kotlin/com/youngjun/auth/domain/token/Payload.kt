package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.support.EPOCH
import java.time.LocalDateTime

data class Payload(
    val userId: Long,
    val expiration: LocalDateTime,
) {
    companion object {
        val Empty: Payload = Payload(0, EPOCH)

        fun from(
            subject: String,
            expiration: LocalDateTime,
        ): Payload = Payload(subject.toLong(), expiration)
    }
}
