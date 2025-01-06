package com.youngjun.auth.core.domain.token

data class TokenPairBuilder(
    var authId: Long = 1,
    var accessToken: String =
        "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzU5Njk2OTAsImV4cCI6MTczNTk3MTQ5MH0.DRnoLwFpmwER9VoCmbyR-tIUIJSrRjOufzAsR3G3miA",
    var accessTokenExpiresIn: Long = 17359714909720,
    var refreshToken: String =
        "eyJhbGciOiJIUzI1NiJ9.eyJpYXQiOjE3MzU5Njk2OTAsImV4cCI6MTczODU2MTY5MH0.vhoGUbS5qZzlIgjz7cwCQaoqG7P0iJR9pEUCYbDwSbg",
    var refreshTokenExpiresIn: Long = 1738561690972,
) {
    fun build(): TokenPair =
        TokenPair(
            authId = authId,
            accessToken = accessToken,
            accessTokenExpiration = accessTokenExpiresIn,
            refreshToken = refreshToken,
            refreshTokenExpiration = refreshTokenExpiresIn,
        )
}
