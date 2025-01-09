package com.youngjun.auth.core.api.controller.v1

import com.youngjun.auth.core.api.application.UserService
import com.youngjun.auth.core.api.controller.v1.request.RegisterUserRequest
import com.youngjun.auth.core.api.controller.v1.response.RegisterUserResponse
import com.youngjun.auth.core.api.support.response.AuthResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService,
) {
    @PostMapping("/auth/register")
    fun register(
        @RequestBody request: RegisterUserRequest,
    ): AuthResponse<RegisterUserResponse> {
        val user = userService.register(request.toNewUser())
        return AuthResponse.success(RegisterUserResponse.from(user))
    }
}
