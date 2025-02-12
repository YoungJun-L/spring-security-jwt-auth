package com.youngjun.auth.domain.account

data class AccountBuilder(
    val email: Email = EmailBuilder().build(),
    val password: Password = PasswordBuilder().build(),
    val status: AccountStatus = AccountStatus.ENABLED,
) {
    fun build(): Account = Account(email = email, password = password, status = status)
}

data class EmailBuilder(
    val value: String = "example@youngjun.com",
) {
    fun build(): Email = Email(value = value)
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
