package com.youngjun.auth.domain.account

import org.springframework.security.crypto.password.PasswordEncoder

data class AccountBuilder(
    val emailAddress: EmailAddress = EmailAddressBuilder().build(),
    val password: Password = PasswordBuilder().build(),
    val status: AccountStatus = AccountStatus.ENABLED,
) {
    fun build(): Account = Account(emailAddress = emailAddress, password = password, status = status)
}

data class EmailAddressBuilder(
    val value: String = "example@youngjun.com",
) {
    fun build(): EmailAddress = EmailAddress(value = value)
}

data class PasswordBuilder(
    val rawPassword: RawPassword = RawPasswordBuilder().build(),
    val passwordEncoder: PasswordEncoder = NoOperationPasswordEncoder,
) {
    fun build(): Password = Password.encodedWith(rawPassword, passwordEncoder)
}

data class RawPasswordBuilder(
    val value: String = "password123!",
) {
    fun build(): RawPassword = RawPassword(value = value)
}

object NoOperationPasswordEncoder : PasswordEncoder {
    override fun encode(rawPassword: CharSequence): String = "NoOp$rawPassword"

    override fun matches(
        rawPassword: CharSequence,
        encodedPassword: String,
    ): Boolean = "NoOp$rawPassword" == encodedPassword
}
