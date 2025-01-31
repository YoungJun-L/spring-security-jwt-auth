package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.token.TokenStatus.ENABLED
import com.youngjun.auth.domain.token.TokenStatus.EXPIRED
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.TOKEN_EXPIRED_ERROR

data class RefreshToken(
    val id: Long,
    val userId: Long,
    val value: String,
    val status: TokenStatus,
) {
    fun verify() {
        when (status) {
            EXPIRED -> throw AuthException(TOKEN_EXPIRED_ERROR)
            ENABLED -> return
        }
    }
}
