package com.youngjun.auth.core.domain.token

data class NewToken(
    val userId: Long,
    val refreshToken: String,
) {
    companion object {
        fun from(tokenPair: TokenPair): NewToken = NewToken(tokenPair.userId, tokenPair.refreshToken)
    }
}
