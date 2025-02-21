package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.infra.db.VerificationCodeJpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class VerificationCodeRepository(
    private val verificationCodeJpaRepository: VerificationCodeJpaRepository,
) {
    fun save(verificationCode: VerificationCode): VerificationCode = verificationCodeJpaRepository.save(verificationCode)

    fun countSince(
        emailAddress: EmailAddress,
        since: LocalDateTime,
    ): Int = verificationCodeJpaRepository.countByEmailAddressAndCreatedAtGreaterThanEqual(emailAddress, since)

    fun findLatestBy(emailAddress: EmailAddress): VerificationCode? =
        verificationCodeJpaRepository.findFirstByEmailAddressOrderByCreatedAtDesc(emailAddress)
}
