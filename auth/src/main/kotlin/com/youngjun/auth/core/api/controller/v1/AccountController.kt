package com.youngjun.auth.core.api.controller.v1

import com.youngjun.auth.core.api.application.AccountService
import com.youngjun.auth.core.api.controller.v1.request.RegisterAccountRequest
import com.youngjun.auth.core.api.controller.v1.response.RegisterAccountResponse
import com.youngjun.auth.core.api.support.response.AuthResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(
    private val accountService: AccountService,
) {
    @PostMapping("/auth/register")
    fun register(
        @RequestBody request: RegisterAccountRequest,
    ): AuthResponse<RegisterAccountResponse> {
        val account = accountService.register(request.toNewAccount())
        return AuthResponse.success(RegisterAccountResponse.from(account))
    }
}
