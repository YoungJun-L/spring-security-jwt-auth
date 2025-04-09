package com.youngjun.auth.domain.verificationCode

import org.springframework.stereotype.Component

@Component
class VerificationCodeWriter(
    private val verificationCodeRepository: VerificationCodeRepository,
) {
    fun write(verificationCode: VerificationCode): VerificationCode = verificationCodeRepository.save(verificationCode)
}
