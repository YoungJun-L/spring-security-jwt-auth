package com.youngjun.auth.infra.mail

import com.youngjun.auth.domain.account.EmailAddressBuilder
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import jakarta.mail.Session
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.mail.javamail.JavaMailSender
import java.util.Properties

class DefaultMailSenderTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val mailSender = mockk<JavaMailSender>()
            val mailProperties =
                MailProperties().apply {
                    username = "username"
                }
            val defaultMailSender = DefaultMailSender(mailSender, mailProperties)

            context("메일 전송") {
                test("성공") {
                    val mimeMessage = MimeMessage(Session.getInstance(Properties()))
                    every { mailSender.createMimeMessage() } returns mimeMessage
                    every { mailSender.send(any<MimeMessage>()) } just Runs
                    val emailAddress = EmailAddressBuilder().build()

                    defaultMailSender.send(emailAddress, "subject", "body")

                    mimeMessage.from[0] shouldBe InternetAddress(mailProperties.username)
                    mimeMessage.allRecipients[0] shouldBe InternetAddress(emailAddress.value)
                    mimeMessage.subject shouldBe "subject"
                    mimeMessage.content.toString() shouldBe "body"
                    verify { mailSender.send(mimeMessage) }
                }
            }
        },
    )
