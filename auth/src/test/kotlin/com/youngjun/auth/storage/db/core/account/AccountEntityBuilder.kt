package com.youngjun.auth.storage.db.core.account

import com.youngjun.auth.core.domain.account.AccountStatus

data class AccountEntityBuilder(
    val username: String = "username123",
    val password: String = "\$2a\$10\$msjhx7NVjB0qoN/k7G2VVuuQNB7PSF/d.WfWYOQJwfyvcDfZIKBte",
    val status: AccountStatus = AccountStatus.ENABLED,
) {
    fun build(): AccountEntity =
        AccountEntity(
            username = username,
            password = password,
            status = status,
        )
}
