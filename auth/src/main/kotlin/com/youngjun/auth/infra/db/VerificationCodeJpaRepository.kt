package com.youngjun.auth.infra.db

import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.verificationCode.VerificationCode
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface VerificationCodeJpaRepository : JpaRepository<VerificationCode, Long> {
    fun countByEmailAddressAndCreatedAtAfter(
        emailAddress: EmailAddress,
        createdAt: LocalDateTime,
    ): Int
}
