package com.youngjun.auth.core.domain.token

data class RefreshTokenBuilder(
    val value: String = "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzU5Njk2OTAsImV4cCI6MTczODU2MTY5MH0.vhoGUbS5qZzlIgjz7cwCQaoqG7P0iJR9pEUCYbDwSbg",
) {
    fun build(): RefreshToken =
        RefreshToken(
            value = value,
        )
}
