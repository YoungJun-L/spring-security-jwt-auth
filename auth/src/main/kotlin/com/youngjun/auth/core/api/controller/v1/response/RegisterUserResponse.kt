package com.youngjun.auth.core.api.controller.v1.response

import com.youngjun.auth.core.domain.user.User

data class RegisterUserResponse(
    val userId: Long,
) {
    companion object {
        fun from(user: User): RegisterUserResponse = RegisterUserResponse(user.id)
    }
}
