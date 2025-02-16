package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.support.minutes
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.VERIFICATION_CODE_LIMIT_EXCEEDED
import org.springframework.stereotype.Component

@Component
class VerificationCodeReader(
    private val verificationCodeRepository: VerificationCodeRepository,
) {
    fun validateRequestLimitExceeded(emailAddress: EmailAddress) {
        if (verificationCodeRepository.countRecentlySaved(emailAddress, REQUEST_LIMIT_DURATION) >= REQUEST_LIMIT) {
            throw AuthException(VERIFICATION_CODE_LIMIT_EXCEEDED)
        }
    }

    companion object {
        private const val REQUEST_LIMIT = 5
        private val REQUEST_LIMIT_DURATION = 10.minutes
    }
}
