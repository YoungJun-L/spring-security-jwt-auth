package com.youngjun.auth.core.domain.auth

data class NewAuthBuilder(
    val username: String = "username123",
    val password: String = "password123!",
) {
    fun build(): NewAuth =
        NewAuth(
            username = username,
            password = password,
        )
}
