package com.youngjun.auth.domain.account

import org.springframework.security.crypto.password.PasswordEncoder

data class AccountBuilder(
    val emailAddress: EmailAddress = EMAIL_ADDRESS,
    val password: Password = Password.encodedWith(RAW_PASSWORD, NoOperationPasswordEncoder),
    val status: AccountStatus = AccountStatus.ENABLED,
) {
    fun build(): Account = Account(emailAddress = emailAddress, password = password, status = status)
}

object NoOperationPasswordEncoder : PasswordEncoder {
    override fun encode(rawPassword: CharSequence): String = "NoOp$rawPassword"

    override fun matches(
        rawPassword: CharSequence,
        encodedPassword: String,
    ): Boolean = "NoOp$rawPassword" == encodedPassword
}

val EMAIL_ADDRESS: EmailAddress = EmailAddress("example@youngjun.com")

val RAW_PASSWORD: RawPassword = RawPassword("password123!")
