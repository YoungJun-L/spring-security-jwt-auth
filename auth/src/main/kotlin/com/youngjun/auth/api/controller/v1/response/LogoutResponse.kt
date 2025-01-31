package com.youngjun.auth.api.controller.v1.response

import com.youngjun.auth.domain.account.Account

data class LogoutResponse(
    val userId: Long,
) {
    companion object {
        fun from(account: Account): LogoutResponse = LogoutResponse(account.id)
    }
}
