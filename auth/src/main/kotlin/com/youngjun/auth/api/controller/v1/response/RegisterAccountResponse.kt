package com.youngjun.auth.api.controller.v1.response

import com.youngjun.auth.domain.account.Account

data class RegisterAccountResponse(
    val userId: Long,
) {
    companion object {
        fun from(account: Account): RegisterAccountResponse = RegisterAccountResponse(account.id)
    }
}
