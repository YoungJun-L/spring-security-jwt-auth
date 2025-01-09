package com.youngjun.auth.core.domain.user

data class UserBuilder(
    var id: Long = 1,
    var username: String = "username123",
    var password: String = "\$2a\$10\$msjhx7NVjB0qoN/k7G2VVuuQNB7PSF/d.WfWYOQJwfyvcDfZIKBte",
    var status: UserStatus = UserStatus.ENABLED,
) {
    fun build(): User =
        User(
            id = id,
            username = username,
            password = password,
            status = status,
        )
}

data class NewUserBuilder(
    val username: String = "username123",
    val password: String = "password123!",
) {
    fun build(): NewUser =
        NewUser(
            username = username,
            password = password,
        )
}
