package com.youngjun.auth.application

import com.youngjun.auth.domain.verificationCode.VerificationCode
import com.youngjun.auth.infra.mail.DefaultMailSender
import org.springframework.stereotype.Service
import org.thymeleaf.TemplateEngine
import org.thymeleaf.context.Context

@Service
class MailService(
    private val mailSender: DefaultMailSender,
    private val templateEngine: TemplateEngine,
) {
    fun sendVerificationCode(verificationCode: VerificationCode) {
        mailSender.send(
            verificationCode.emailAddress,
            "Verification Code",
            templateEngine.process(
                "mail/verification-code.html",
                Context().apply {
                    setVariable("code", verificationCode.code)
                },
            ),
        )
    }
}
