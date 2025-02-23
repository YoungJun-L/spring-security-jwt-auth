package com.youngjun.auth.api.controller.v1

import com.youngjun.auth.api.controller.v1.request.ChangePasswordRequest
import com.youngjun.auth.api.controller.v1.request.RegisterAccountRequest
import com.youngjun.auth.api.controller.v1.response.AccountResponse
import com.youngjun.auth.application.AccountService
import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.security.handler.UserTokenResponse
import com.youngjun.auth.support.response.AuthResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(
    private val accountService: AccountService,
) {
    @PostMapping("/auth/register")
    fun register(
        @RequestBody request: RegisterAccountRequest,
    ): AuthResponse<AccountResponse> {
        val account = accountService.register(request.toEmailAddress(), request.toRawPassword(), request.toRawVerificationCode())
        return AuthResponse.success(AccountResponse.from(account))
    }

    @PutMapping("/account/password")
    fun changePassword(
        account: Account,
        @RequestBody request: ChangePasswordRequest,
    ): AuthResponse<UserTokenResponse> {
        val tokenPair = accountService.changePassword(account, request.toRawPassword())
        return AuthResponse.success(UserTokenResponse.from(tokenPair))
    }
}
