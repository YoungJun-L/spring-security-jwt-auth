package com.youngjun.auth.storage.db.core.token

import com.youngjun.auth.core.domain.token.TokenStatus

data class RefreshTokenEntityBuilder(
    val userId: Long = 1L,
    val token: String =
        "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzU5Njk2OTAsImV4cCI6MTczODU2MTY5MH0.vhoGUbS5qZzlIgjz7cwCQaoqG7P0iJR9pEUCYbDwSbg",
    val status: TokenStatus = TokenStatus.ENABLED,
) {
    fun build(): RefreshTokenEntity =
        RefreshTokenEntity(
            userId = userId,
            token = token,
            status = status,
        )
}
