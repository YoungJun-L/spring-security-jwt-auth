package com.youngjun.auth.domain.account

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Email(
    @Column(name = "email")
    val value: String,
) {
    init {
        require(emailRegex.matches(value)) { "Email validation error" }
    }

    companion object {
        private val emailRegex = Regex("[\\w.-]+@[\\w.-]+\\.\\w{2,4}")
    }
}
