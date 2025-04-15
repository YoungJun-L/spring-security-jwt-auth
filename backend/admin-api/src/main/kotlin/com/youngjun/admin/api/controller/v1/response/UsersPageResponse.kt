package com.youngjun.admin.api.controller.v1.response

import com.youngjun.admin.domain.user.AdminUser

data class UsersPageResponse(
    val users: List<UserResponse>,
    val lastId: Long = -1,
) {
    companion object {
        fun of(
            adminUsers: List<AdminUser>,
            lastId: Long,
        ): UsersPageResponse = UsersPageResponse(adminUsers.map { UserResponse.from(it) }, lastId)
    }
}
