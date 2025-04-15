package com.youngjun.admin.api.controller.v1.response

import com.youngjun.admin.domain.user.AdminUserCount

data class UserCountResponse(
    val totalUserCount: Long,
    val activeUserCount: Long,
    val inactiveUserCount: Long,
) {
    companion object {
        fun from(userCount: AdminUserCount): UserCountResponse =
            UserCountResponse(
                totalUserCount = userCount.totalUser,
                activeUserCount = userCount.activeUser,
                inactiveUserCount = userCount.inactiveUser,
            )
    }
}
