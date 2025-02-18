package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.support.minutes
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.VERIFICATION_CODE_LIMIT_EXCEEDED
import com.youngjun.auth.support.error.ErrorType.VERIFICATION_CODE_NOT_FOUND
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class VerificationCodeReader(
    private val verificationCodeRepository: VerificationCodeRepository,
) {
    fun checkRecentSavesExceeded(
        emailAddress: EmailAddress,
        now: LocalDateTime = LocalDateTime.now(),
    ) {
        if (verificationCodeRepository.countSince(emailAddress, now - RECENT_DURATION) >= COUNT_LIMIT) {
            throw AuthException(VERIFICATION_CODE_LIMIT_EXCEEDED)
        }
    }

    fun readLatest(emailAddress: EmailAddress): VerificationCode =
        verificationCodeRepository.findLatestBy(emailAddress) ?: throw AuthException(VERIFICATION_CODE_NOT_FOUND)

    companion object {
        private const val COUNT_LIMIT = 5
        private val RECENT_DURATION = 10.minutes
    }
}
