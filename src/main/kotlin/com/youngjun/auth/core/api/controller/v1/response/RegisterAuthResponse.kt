package com.youngjun.auth.core.api.controller.v1.response

import com.youngjun.auth.core.domain.auth.Auth

data class RegisterAuthResponse(
    val userId: Long,
) {
    companion object {
        fun from(auth: Auth): RegisterAuthResponse = RegisterAuthResponse(auth.id)
    }
}
