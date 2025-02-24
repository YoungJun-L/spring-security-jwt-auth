package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EmailAddress
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

fun VerificationCodeRepository.countSince(
    emailAddress: EmailAddress,
    since: LocalDateTime,
): Int = countByEmailAddressAndCreatedAtGreaterThanEqual(emailAddress, since)

fun VerificationCodeRepository.findLatestBy(emailAddress: EmailAddress): VerificationCode? =
    findFirstByEmailAddressOrderByCreatedAtDesc(emailAddress)

interface VerificationCodeRepository : JpaRepository<VerificationCode, Long> {
    fun countByEmailAddressAndCreatedAtGreaterThanEqual(
        emailAddress: EmailAddress,
        createdAt: LocalDateTime,
    ): Int

    fun findFirstByEmailAddressOrderByCreatedAtDesc(emailAddress: EmailAddress): VerificationCode?
}
