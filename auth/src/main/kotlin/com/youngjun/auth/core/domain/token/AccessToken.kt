package com.youngjun.auth.core.domain.token

data class AccessToken(
    val token: Token,
) {
    val value: String get() = token.value

    companion object {
        operator fun invoke(value: String): AccessToken = AccessToken(Token(value))
    }
}
