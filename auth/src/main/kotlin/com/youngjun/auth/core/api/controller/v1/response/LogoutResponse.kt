package com.youngjun.auth.core.api.controller.v1.response

import com.youngjun.auth.core.domain.account.Account

data class LogoutResponse(
    val userId: Long,
) {
    companion object {
        fun from(account: Account): LogoutResponse = LogoutResponse(account.id)
    }
}
