package com.youngjun.auth.infra.mail

import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component

@Component
class DefaultMailSender(
    private val mailSender: JavaMailSender,
    private val mailProperties: MailProperties,
) {
    fun send(
        toAddress: String,
        subject: String,
        body: String,
    ) {
        val message = mailSender.createMimeMessage()
        MimeMessageHelper(message).apply {
            setFrom(mailProperties.username)
            setTo(toAddress)
            setSubject(subject)
            setText(body, true)
        }
        mailSender.send(message)
    }
}
