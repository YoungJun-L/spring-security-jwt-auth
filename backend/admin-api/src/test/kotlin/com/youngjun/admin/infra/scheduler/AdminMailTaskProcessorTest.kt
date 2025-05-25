package com.youngjun.admin.infra.scheduler

import com.youngjun.admin.domain.mail.MailMessageInfo
import com.youngjun.admin.domain.mail.MailTask
import com.youngjun.admin.domain.mail.MailTaskRepository
import com.youngjun.admin.domain.mail.MailTaskStatus
import com.youngjun.admin.domain.support.RetryInfo
import com.youngjun.admin.domain.template.TemplateBuilder
import com.youngjun.admin.domain.template.TemplateRepository
import com.youngjun.admin.domain.user.EmailAddress
import com.youngjun.admin.infra.mail.AdminMailSender
import com.youngjun.admin.support.ApplicationContextTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.verify
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

@ApplicationContextTest
class AdminMailTaskProcessorTest(
    private val adminMailTaskProcessor: AdminMailTaskProcessor,
    private val mailTaskRepository: MailTaskRepository,
    private val templateRepository: TemplateRepository,
    private val mailSender: AdminMailSender,
) : FunSpec({
        extensions(SpringExtension)
        isolationMode = IsolationMode.InstancePerLeaf

        context("작업 실행") {
            test("성공") {
                val template = templateRepository.save(TemplateBuilder().build())
                val task =
                    mailTaskRepository.save(
                        MailTask(
                            mailMessageInfo = MailMessageInfo(template.id, EmailAddress("test1@example.com"), mapOf("name" to "철수")),
                            status = MailTaskStatus.PENDING,
                        ),
                    )

                adminMailTaskProcessor.processPendingTasks()

                verify { mailSender.send(task.mailMessageInfo.recipient, any()) }
                mailTaskRepository.findByIdOrNull(task.id)!!.status shouldBe MailTaskStatus.SENT
            }
        }

        context("재시도 가능한 실패 작업 실행") {
            test("성공") {
                val template = templateRepository.save(TemplateBuilder().build())
                val task =
                    mailTaskRepository.save(
                        MailTask(
                            mailMessageInfo = MailMessageInfo(template.id, EmailAddress("test1@example.com"), mapOf("name" to "철수")),
                            status = MailTaskStatus.FAILED,
                            retryInfo = RetryInfo(1, LocalDateTime.now().minusMinutes(1)),
                        ),
                    )

                adminMailTaskProcessor.processFailedTasks()

                verify { mailSender.send(task.mailMessageInfo.recipient, any()) }
                mailTaskRepository.findByIdOrNull(task.id)!!.status shouldBe MailTaskStatus.SENT
            }
        }
    })
