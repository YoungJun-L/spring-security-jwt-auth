package com.youngjun.auth.domain.account

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class EmailAddress(
    @Column(name = "email")
    val value: String,
) {
    init {
        require(emailAddressRegex.matches(value)) { "Email address validation error" }
    }

    companion object {
        private val emailAddressRegex = Regex("[\\w.-]+@[\\w.-]+\\.\\w{2,4}")
    }
}
