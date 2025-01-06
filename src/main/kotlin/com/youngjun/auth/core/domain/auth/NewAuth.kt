package com.youngjun.auth.core.domain.auth

data class NewAuth(
    val username: String,
    val password: String,
) {
    fun encoded(encodedPassword: String): NewAuth = NewAuth(username, encodedPassword)
}
