package com.youngjun.auth.domain.account

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

private val emailAddressRegex = Regex("[\\w.-]+@[\\w.-]+\\.\\w{2,4}")

@Embeddable
data class EmailAddress private constructor(
    @Column(name = "email")
    val value: String,
) {
    companion object {
        fun from(value: String): EmailAddress {
            require(emailAddressRegex.matches(value)) { "Email address validation error" }
            return EmailAddress(value)
        }
    }
}
