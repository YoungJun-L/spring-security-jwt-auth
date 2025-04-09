package com.youngjun.admin.api.controller.v1.response

import com.youngjun.admin.domain.user.EmailAddress
import com.youngjun.admin.domain.user.User
import com.youngjun.admin.domain.user.UserStatus
import java.time.LocalDateTime

data class UserResponse(
    val id: Long,
    val email: EmailAddress,
    val status: UserStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(user: User): UserResponse = UserResponse(user.id, user.emailAddress, user.status, user.createdAt, user.updatedAt)
    }
}
