package com.youngjun.auth.storage.db.core.token

data class TokenEntityBuilder(
    val userId: Long = 1L,
    val refreshToken: String =
        "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzU5Njk2OTAsImV4cCI6MTczODU2MTY5MH0.vhoGUbS5qZzlIgjz7cwCQaoqG7P0iJR9pEUCYbDwSbg",
) {
    fun build(): TokenEntity =
        TokenEntity(
            userId = userId,
            refreshToken = refreshToken,
        )
}
