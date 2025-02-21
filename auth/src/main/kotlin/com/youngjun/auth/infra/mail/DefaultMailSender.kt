package com.youngjun.auth.infra.mail

import com.youngjun.auth.domain.account.EmailAddress
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class DefaultMailSender(
    private val mailSender: JavaMailSender,
    private val mailProperties: MailProperties,
) {
    @Async
    fun send(
        toEmailAddress: EmailAddress,
        subject: String,
        body: String,
    ) {
        val message = mailSender.createMimeMessage()
        MimeMessageHelper(message).apply {
            setFrom(mailProperties.username)
            setTo(toEmailAddress.value)
            setSubject(subject)
            setText(body, true)
        }
        mailSender.send(message)
    }
}
