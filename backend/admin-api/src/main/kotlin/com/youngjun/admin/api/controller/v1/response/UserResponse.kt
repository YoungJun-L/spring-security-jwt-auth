package com.youngjun.admin.api.controller.v1.response

import com.youngjun.admin.domain.user.AdminUser
import com.youngjun.admin.domain.user.EmailAddress
import com.youngjun.admin.domain.user.UserStatus
import java.time.LocalDateTime

data class UserResponse(
    val id: Long,
    val email: EmailAddress,
    val profile: ProfileResponse,
    val status: UserStatus,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
) {
    companion object {
        fun from(adminUser: AdminUser): UserResponse =
            UserResponse(
                adminUser.id,
                adminUser.emailAddress,
                ProfileResponse.from(adminUser.profile),
                adminUser.status,
                adminUser.createdAt,
                adminUser.updatedAt,
            )
    }
}
