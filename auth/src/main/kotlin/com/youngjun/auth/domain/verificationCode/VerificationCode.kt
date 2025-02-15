package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.support.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Table(name = "verification_code")
@Entity
class VerificationCode(
    @Embedded
    val emailAddress: EmailAddress,
    @Column
    val code: Int,
    isVerified: Boolean = false,
) : BaseEntity() {
    @Column
    var isVerified = isVerified
        private set

    fun verify() {
        isVerified = true
    }
}
