package com.youngjun.auth.api.controller.v1

import com.youngjun.auth.api.controller.v1.request.ResetPasswordRequest
import com.youngjun.auth.api.controller.v1.request.SendVerificationCodeRequest
import com.youngjun.auth.application.MailService
import com.youngjun.auth.application.VerificationCodeService
import com.youngjun.auth.support.response.AuthResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class VerificationCodeController(
    private val verificationCodeService: VerificationCodeService,
    private val mailService: MailService,
) {
    @PostMapping("/auth/send-verification-code")
    fun sendVerificationCode(
        @RequestBody request: SendVerificationCodeRequest,
    ): AuthResponse<Unit> {
        val verificationCode = verificationCodeService.generateForNewAccount(request.email)
        mailService.sendVerificationCode(verificationCode)
        return AuthResponse.success()
    }

    @PostMapping("/auth/reset-password")
    fun resetPassword(
        @RequestBody request: ResetPasswordRequest,
    ): AuthResponse<Unit> {
        val verificationCode = verificationCodeService.generateForExistingAccount(request.email)
        mailService.sendVerificationCode(verificationCode)
        return AuthResponse.success()
    }
}
