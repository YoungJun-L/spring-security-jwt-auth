package com.youngjun.auth.core.domain.token

data class RefreshToken(
    val token: Token,
) {
    val value: String get() = token.value

    companion object {
        val Empty: RefreshToken = RefreshToken(Token(""))

        operator fun invoke(value: String): RefreshToken = RefreshToken(Token(value))
    }
}
