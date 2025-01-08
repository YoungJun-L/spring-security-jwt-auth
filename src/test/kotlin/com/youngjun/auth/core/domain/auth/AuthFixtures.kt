package com.youngjun.auth.core.domain.auth

data class AuthBuilder(
    var id: Long = 1,
    var username: String = "username123",
    var password: String = "\$2a\$10\$msjhx7NVjB0qoN/k7G2VVuuQNB7PSF/d.WfWYOQJwfyvcDfZIKBte",
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
