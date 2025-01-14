package com.youngjun.auth.core.api.controller.v1

import com.youngjun.auth.core.api.application.TokenService
import com.youngjun.auth.core.api.controller.v1.request.ReissueTokenRequest
import com.youngjun.auth.core.api.controller.v1.response.ReissueTokenResponse
import com.youngjun.auth.core.api.support.response.AuthResponse
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
    ): AuthResponse<ReissueTokenResponse> {
        val tokenPair = tokenService.reissue(request.toRefreshToken())
        return AuthResponse.success(ReissueTokenResponse.from(tokenPair))
    }
}
