package com.youngjun.auth.domain.account

private val emailAddressRegex = Regex("[\\w.-]+@[\\w.-]+\\.\\w{2,4}")

@JvmInline
value class EmailAddress(
    val value: String,
) {
    init {
        require(emailAddressRegex.matches(value)) { "Email address validation error" }
    }
}
