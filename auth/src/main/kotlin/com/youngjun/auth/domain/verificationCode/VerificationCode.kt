package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.Email
import com.youngjun.auth.domain.support.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Table(name = "verification_code")
@Entity
class VerificationCode(
    @Embedded
    val email: Email,
    @Column
    val code: Int,
    isVerified: Boolean,
) : BaseEntity() {
    @Column
    var isVerified = isVerified
        private set

    fun verify() {
        isVerified = true
    }
}
