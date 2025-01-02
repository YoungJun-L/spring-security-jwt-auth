package com.youngjun.auth.core.domain.auth

data class AuthBuilder(
    var id: Long = 0,
    var username: String = "",
    var password: String = "",
    var status: AuthStatus = AuthStatus.ENABLED,
) {
    fun build(): Auth =
        Auth(
            id = id,
            username = username,
            password = password,
            status = status,
        )
}
