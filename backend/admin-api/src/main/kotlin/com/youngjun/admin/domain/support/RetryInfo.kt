package com.youngjun.admin.domain.support

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import java.time.LocalDateTime
import kotlin.math.pow

@Embeddable
data class RetryInfo(
    @Column
    val retryCount: Int = 0,
    @Column
    val nextRetryAt: LocalDateTime? = null,
) {
    fun onFailure(now: LocalDateTime = LocalDateTime.now()): RetryInfo {
        val backoffMinutes = 2.0.pow(retryCount).toLong()
        return this.copy(
            retryCount = this.retryCount + 1,
            nextRetryAt = now.plusMinutes(backoffMinutes),
        )
    }

    fun isUnrecoverable(): Boolean = retryCount >= MAX_RETRY

    companion object {
        private const val MAX_RETRY = 3
    }
}
