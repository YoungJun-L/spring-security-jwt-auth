package com.youngjun.admin.application

import com.youngjun.admin.domain.mail.MailTaskRepository
import com.youngjun.admin.domain.mail.MailTaskStatus
import com.youngjun.admin.domain.template.TemplateBuilder
import com.youngjun.admin.domain.template.TemplateRepository
import com.youngjun.admin.domain.template.TemplatedRecipient
import com.youngjun.admin.domain.user.EmailAddress
import com.youngjun.admin.support.ApplicationContextTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

@ApplicationContextTest
class AdminMailServiceTest(
    private val adminMailService: AdminMailService,
    private val mailTaskRepository: MailTaskRepository,
    private val templateRepository: TemplateRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("메일 전송 작업 제출") {
                test("성공") {
                    val template = templateRepository.save(TemplateBuilder().build())
                    val recipients =
                        listOf(
                            TemplatedRecipient(
                                email = EmailAddress("user1@example.com"),
                                variables = mapOf("name" to "철수"),
                            ),
                            TemplatedRecipient(
                                email = EmailAddress("user2@example.com"),
                                variables = mapOf("name" to "영희"),
                            ),
                        )

                    adminMailService.submit(template.id, recipients)

                    val tasks = mailTaskRepository.findAll()
                    tasks shouldHaveSize recipients.size
                    recipients.forEach { recipient ->
                        with(tasks.first { it.mailMessageInfo.recipient == recipient.email }) {
                            mailMessageInfo.templateId shouldBe template.id
                            mailMessageInfo.variables shouldBe recipient.variables
                            status shouldBe MailTaskStatus.PENDING
                        }
                    }
                }
            }
        },
    )
