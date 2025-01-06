package com.youngjun.auth.core.storage.db.core.auth

import com.youngjun.auth.core.domain.auth.AuthStatus
import com.youngjun.auth.storage.db.core.auth.AuthEntity

data class AuthEntityBuilder(
    val username: String = "username123",
    val password: String = "\$2a\$10\$msjhx7NVjB0qoN/k7G2VVuuQNB7PSF/d.WfWYOQJwfyvcDfZIKBte",
    val status: AuthStatus = AuthStatus.ENABLED,
) {
    fun build(): AuthEntity =
        AuthEntity(
            username = username,
            password = password,
            status = status,
        )
}
