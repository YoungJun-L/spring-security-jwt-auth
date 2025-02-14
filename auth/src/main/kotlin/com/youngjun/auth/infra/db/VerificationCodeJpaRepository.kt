package com.youngjun.auth.infra.db

import com.youngjun.auth.domain.verificationCode.VerificationCode
import org.springframework.data.jpa.repository.JpaRepository

interface VerificationCodeJpaRepository : JpaRepository<VerificationCode, Long>
