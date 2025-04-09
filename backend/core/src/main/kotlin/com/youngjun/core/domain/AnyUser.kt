package com.youngjun.core.domain

data class AnyUser(
    val id: Long,
) {
    fun toUser(): User = User(id)
}
