package com.youngjun.auth.core.domain.token

data class NewRefreshToken(
    val userId: Long,
    val refreshToken: RefreshToken,
) {
    companion object {
        fun from(tokenPairDetails: TokenPairDetails): NewRefreshToken =
            NewRefreshToken(tokenPairDetails.userId, tokenPairDetails.refreshToken)
    }
}
