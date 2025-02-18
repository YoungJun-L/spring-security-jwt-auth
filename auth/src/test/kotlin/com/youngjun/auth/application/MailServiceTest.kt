package com.youngjun.auth.application

import com.youngjun.auth.domain.verificationCode.generateVerificationCode
import com.youngjun.auth.infra.mail.DefaultMailSender
import com.youngjun.auth.support.ApplicationTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import org.thymeleaf.TemplateEngine

@ApplicationTest
class MailServiceTest :
    FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            val mailSender = mockk<DefaultMailSender>()
            val templateEngine = mockk<TemplateEngine>()
            val mailService = MailService(mailSender, templateEngine)

            context("메일 전송") {
                test("성공") {
                    val verificationCode = generateVerificationCode()
                    val result = "result"
                    every { templateEngine.process(any<String>(), any()) } returns result
                    every { mailSender.send(any(), any(), any()) } just Runs

                    mailService.sendVerificationCode(verificationCode)

                    verify { templateEngine.process("mail/verification-code.html", any()) }
                    verify { mailSender.send(verificationCode.emailAddress, "Verification Code", result) }
                }
            }
        },
    )
