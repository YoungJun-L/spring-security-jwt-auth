package com.youngjun.auth.api.controller.v1.request

import com.youngjun.auth.domain.account.RawPassword

data class ChangePasswordRequest(
    val password: String,
) {
    fun toRawPassword(): RawPassword = RawPassword(password)
}
