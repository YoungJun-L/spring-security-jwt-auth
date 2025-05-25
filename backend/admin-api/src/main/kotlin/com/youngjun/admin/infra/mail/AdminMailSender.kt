package com.youngjun.admin.infra.mail

import com.youngjun.admin.domain.template.MailContent
import com.youngjun.admin.domain.user.EmailAddress
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component

@Component
class AdminMailSender(
    private val mailSender: JavaMailSender,
    private val mailProperties: MailProperties,
) {
    fun send(
        recipient: EmailAddress,
        content: MailContent,
    ) {
        val message = mailSender.createMimeMessage()
        MimeMessageHelper(message).apply {
            setFrom(mailProperties.username)
            setTo(recipient.value)
            setSubject(content.subject)
            setText(content.body, true)
        }
        mailSender.send(message)
    }
}
