package com.youngjun.auth.api.controller.v1.request

import com.youngjun.auth.domain.account.EmailAddress

data class ResetPasswordRequest(
    val email: EmailAddress,
)
