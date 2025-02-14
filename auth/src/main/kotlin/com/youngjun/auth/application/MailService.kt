package com.youngjun.auth.application

import com.youngjun.auth.domain.account.Email
import com.youngjun.auth.domain.verificationCode.VerificationCode
import com.youngjun.auth.infra.mail.DefaultMailSender
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine

@Service
class MailService(
    private val mailSender: DefaultMailSender,
    private val templateEngine: TemplateEngine,
) {
    fun sendVerificationCode(
        email: Email,
        verificationCode: VerificationCode,
    ) {
        TODO("Not yet implemented")
    }
}
