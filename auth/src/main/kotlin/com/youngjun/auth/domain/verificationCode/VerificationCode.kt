package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.support.BaseEntity
import com.youngjun.auth.domain.support.minutes
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.VERIFICATION_CODE_EXPIRED
import com.youngjun.auth.support.error.ErrorType.VERIFICATION_CODE_MISMATCHED
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "verification_code")
@Entity
class VerificationCode private constructor(
    @Column(name = "email")
    val emailAddress: EmailAddress,
    @Column
    val code: RawVerificationCode,
) : BaseEntity() {
    fun verifyWith(
        rawVerificationCode: RawVerificationCode,
        now: LocalDateTime = LocalDateTime.now(),
    ) {
        fun isMismatched() = code != rawVerificationCode

        fun isExpired() = createdAt + 10.minutes <= now

        when {
            isMismatched() -> throw AuthException(VERIFICATION_CODE_MISMATCHED)
            isExpired() -> throw AuthException(VERIFICATION_CODE_EXPIRED)
        }
    }

    companion object {
        fun generate(emailAddress: EmailAddress): VerificationCode =
            VerificationCode(emailAddress, RawVerificationCode((0..<1_000_000).random().toString().padStart(6, '0')))
    }
}
