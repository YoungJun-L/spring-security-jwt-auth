package com.youngjun.auth.api.controller.v1

import com.youngjun.auth.api.controller.v1.request.ReissueTokenRequest
import com.youngjun.auth.api.controller.v1.response.TokenPairResponse
import com.youngjun.auth.application.TokenService
import com.youngjun.auth.support.response.AuthResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class TokenController(
    private val tokenService: TokenService,
) {
    @PostMapping("/auth/token")
    fun reissue(
        @RequestBody request: ReissueTokenRequest,
    ): AuthResponse<TokenPairResponse> {
        val tokenPair = tokenService.reissue(request.refreshToken)
        return AuthResponse.success(TokenPairResponse.from(tokenPair))
    }
}
