package com.youngjun.auth.api.controller.v1.request

import com.youngjun.auth.domain.account.Email

data class SendVerificationCodeRequest(
    val email: String,
) {
    fun toEmail(): Email = Email(email)
}
