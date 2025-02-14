package com.youngjun.auth.api.controller.v1

import com.youngjun.auth.api.controller.v1.request.ChangePasswordRequest
import com.youngjun.auth.api.controller.v1.request.RegisterAccountRequest
import com.youngjun.auth.api.controller.v1.request.SendVerificationCodeRequest
import com.youngjun.auth.api.controller.v1.response.AccountResponse
import com.youngjun.auth.application.AccountService
import com.youngjun.auth.application.MailService
import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.support.response.AuthResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AccountController(
    private val accountService: AccountService,
    private val mailService: MailService,
) {
    @PostMapping("/auth/register")
    fun register(
        @RequestBody request: RegisterAccountRequest,
    ): AuthResponse<AccountResponse> {
        val account = accountService.register(request.toEmail(), request.toRawPassword())
        return AuthResponse.success(AccountResponse.from(account))
    }

    @PostMapping("/auth/send-verification-code")
    fun sendVerificationCode(
        @RequestBody request: SendVerificationCodeRequest,
    ): AuthResponse<Any> {
        val email = request.toEmail()
        val verificationCode = accountService.generateVerificationCode(email)
        mailService.sendVerificationCode(email, verificationCode)
        return AuthResponse.success()
    }

    @PutMapping("/account/password")
    fun changePassword(
        account: Account,
        @RequestBody request: ChangePasswordRequest,
    ): AuthResponse<AccountResponse> {
        val changedAccount = accountService.changePassword(account, request.toRawPassword())
        return AuthResponse.success(AccountResponse.from(changedAccount))
    }
}
