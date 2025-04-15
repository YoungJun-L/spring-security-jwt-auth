package com.youngjun.admin.domain.user

data class AdminUserCount(
    val totalUser: Long,
    val activeUser: Long,
    val inactiveUser: Long,
)
