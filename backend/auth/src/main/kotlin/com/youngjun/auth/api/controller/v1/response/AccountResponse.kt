package com.youngjun.auth.api.controller.v1.response

import com.youngjun.auth.domain.account.Account

data class AccountResponse(
    val userId: Long,
) {
    companion object {
        fun from(account: Account): AccountResponse = AccountResponse(account.id)
    }
}
