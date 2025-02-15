package com.youngjun.auth.api.controller.v1.request

import com.youngjun.auth.domain.account.EmailAddress

data class SendVerificationCodeRequest(
    val email: String,
) {
    fun toEmailAddress(): EmailAddress = EmailAddress(email)
}
