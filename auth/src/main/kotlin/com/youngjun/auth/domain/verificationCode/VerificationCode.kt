package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.support.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Table(name = "verification_code")
@Entity
class VerificationCode protected constructor(
    @Embedded
    val emailAddress: EmailAddress,
    @Column
    val code: String,
) : BaseEntity() {
    companion object {
        fun generate(emailAddress: EmailAddress): VerificationCode =
            VerificationCode(emailAddress, (0..<1_000_000).random().toString().padStart(6, '0'))
    }
}
