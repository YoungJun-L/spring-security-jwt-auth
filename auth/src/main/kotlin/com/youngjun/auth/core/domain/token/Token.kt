package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_EXPIRED_ERROR

data class Token(
    val id: Long,
    val userId: Long,
    val refreshToken: String,
    val status: TokenStatus,
) {
    fun verify() {
        if (status == TokenStatus.EXPIRED) {
            throw AuthException(TOKEN_EXPIRED_ERROR)
        }
    }
}
