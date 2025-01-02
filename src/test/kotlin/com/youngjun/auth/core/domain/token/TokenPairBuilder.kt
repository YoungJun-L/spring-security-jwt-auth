package com.youngjun.auth.core.domain.token

data class TokenPairBuilder(
    var authId: Long = 0,
    var accessToken: String = "",
    var accessTokenExpiresIn: Long = 0,
    var refreshToken: String = "",
    var refreshTokenExpiresIn: Long = 0,
) {
    fun build(): TokenPair =
        TokenPair(
            authId = authId,
            accessToken = accessToken,
            accessTokenExpiresIn = accessTokenExpiresIn,
            refreshToken = refreshToken,
            refreshTokenExpiresIn = refreshTokenExpiresIn,
        )
}
