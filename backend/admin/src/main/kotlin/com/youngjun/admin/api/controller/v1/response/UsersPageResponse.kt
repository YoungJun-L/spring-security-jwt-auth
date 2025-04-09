package com.youngjun.admin.api.controller.v1.response

import com.youngjun.admin.domain.user.User

data class UsersPageResponse(
    val users: List<UserResponse>,
    val lastId: Long = -1,
) {
    companion object {
        fun of(users: List<User>): UsersPageResponse = UsersPageResponse(users.map { UserResponse.from(it) })
    }
}
