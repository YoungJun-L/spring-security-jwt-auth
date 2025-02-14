package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.infra.db.VerificationCodeJpaRepository
import org.springframework.stereotype.Repository

@Repository
class VerificationCodeRepository(
    private val verificationCodeJpaRepository: VerificationCodeJpaRepository,
)
