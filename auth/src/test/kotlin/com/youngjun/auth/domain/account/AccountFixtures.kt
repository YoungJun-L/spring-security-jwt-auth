package com.youngjun.auth.domain.account

data class AccountBuilder(
    val email: Email = EmailBuilder().build(),
    val password: String = "\$2a\$10\$msjhx7NVjB0qoN/k7G2VVuuQNB7PSF/d.WfWYOQJwfyvcDfZIKBte",
    val status: AccountStatus = AccountStatus.ENABLED,
) {
    fun build(): Account =
        Account(
            email = email,
            password = password,
            status = status,
        )
}

data class EmailBuilder(
    val value: String = "example@youngjun.com",
) {
    fun build(): Email = Email(value = value)
}
