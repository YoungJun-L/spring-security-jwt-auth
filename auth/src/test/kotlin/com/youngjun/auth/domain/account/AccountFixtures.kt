package com.youngjun.auth.domain.account

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
    val value: String = "\$2a\$10\$msjhx7NVjB0qoN/k7G2VVuuQNB7PSF/d.WfWYOQJwfyvcDfZIKBte",
) {
    fun build(): Password = Password(value = value)
}

data class RawPasswordBuilder(
    val value: String = "password123!",
) {
    fun build(): RawPassword = RawPassword(value = value)
}
