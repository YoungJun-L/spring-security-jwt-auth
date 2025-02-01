package com.youngjun.auth.domain.account

data class AccountBuilder(
    var username: String = "username123",
    var password: String = "\$2a\$10\$msjhx7NVjB0qoN/k7G2VVuuQNB7PSF/d.WfWYOQJwfyvcDfZIKBte",
    var status: AccountStatus = AccountStatus.ENABLED,
) {
    fun build(): Account =
        Account(
            username = username,
            password = password,
            status = status,
        )
}

data class NewAccountBuilder(
    val username: String = "username123",
    val password: String = "password123!",
) {
    fun build(): NewAccount =
        NewAccount(
            username = username,
            password = password,
        )
}
