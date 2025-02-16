package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.infra.db.VerificationCodeJpaRepository
import org.springframework.stereotype.Repository
import java.time.Duration
import java.time.LocalDateTime

@Repository
class VerificationCodeRepository(
    private val verificationCodeJpaRepository: VerificationCodeJpaRepository,
) {
    fun save(verificationCode: VerificationCode): VerificationCode = verificationCodeJpaRepository.save(verificationCode)

    fun countRecentlySaved(
        emailAddress: EmailAddress,
        duration: Duration,
    ): Int = verificationCodeJpaRepository.countByEmailAddressAndCreatedAtAfter(emailAddress, LocalDateTime.now() - duration)
}
