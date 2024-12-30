package com.youngjun.auth.api.controller.v1

import com.youngjun.auth.api.controller.v1.request.RegisterAuthRequest
import com.youngjun.auth.api.controller.v1.response.RegisterAuthResponse
import com.youngjun.auth.core.api.support.response.AuthResponse
import com.youngjun.auth.core.domain.auth.AuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/auth/register")
    fun register(
        @RequestBody request: RegisterAuthRequest,
    ): AuthResponse<RegisterAuthResponse> {
        val auth = authService.register(request.toNewAuth())
        return AuthResponse.success(RegisterAuthResponse.from(auth))
    }
}
