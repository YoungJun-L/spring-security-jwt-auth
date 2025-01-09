package com.youngjun.auth.core.storage.db.core.user

import com.youngjun.auth.core.domain.user.UserStatus
import com.youngjun.auth.storage.db.core.user.UserEntity

data class UserEntityBuilder(
    val username: String = "username123",
    val password: String = "\$2a\$10\$msjhx7NVjB0qoN/k7G2VVuuQNB7PSF/d.WfWYOQJwfyvcDfZIKBte",
    val status: UserStatus = UserStatus.ENABLED,
) {
    fun build(): UserEntity =
        UserEntity(
            username = username,
            password = password,
            status = status,
        )
}
