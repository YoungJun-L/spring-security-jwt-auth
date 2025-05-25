package com.youngjun.admin.application

import com.youngjun.admin.domain.mail.MailMessageInfo
import com.youngjun.admin.domain.template.MailContent
import com.youngjun.admin.domain.template.TemplateBuilder
import com.youngjun.admin.domain.template.TemplateMeta
import com.youngjun.admin.domain.template.TemplateRepository
import com.youngjun.admin.domain.template.TemplateType
import com.youngjun.admin.domain.template.TemplatedRecipient
import com.youngjun.admin.domain.user.EmailAddress
import com.youngjun.admin.support.ApplicationContextTest
import com.youngjun.admin.support.error.AdminException
import com.youngjun.admin.support.error.ErrorType.TEMPLATE_NOT_FOUND
import com.youngjun.admin.support.error.ErrorType.TEMPLATE_VARIABLES_NOT_MATCH
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.maps.shouldHaveSize
import io.kotest.matchers.shouldBe

@ApplicationContextTest
class AdminTemplateServiceTest(
    private val adminTemplateService: AdminTemplateService,
    private val templateRepository: TemplateRepository,
) : FunSpec({
        extensions(SpringExtension)
        isolationMode = IsolationMode.InstancePerLeaf

        context("템플릿 치환 검증") {
            test("성공") {
                val templateMeta = TemplateMeta(TemplateType("welcome"), 1)
                val variableNames = setOf("name")
                templateRepository.save(TemplateBuilder(templateMeta = templateMeta, variableNames = variableNames).build())

                shouldNotThrowAny {
                    adminTemplateService.validateResolvable(
                        templateMeta,
                        listOf(TemplatedRecipient(email = EmailAddress("user1@example.com"), variables = mapOf("name" to "철수"))),
                    )
                }
            }

            test("템플릿 변수가 일치하지 않으면 실패한다.") {
                val templateMeta = TemplateMeta(TemplateType("welcome"), 1)
                val variableNames = setOf("name")
                templateRepository.save(TemplateBuilder(templateMeta = templateMeta, variableNames = variableNames).build())

                shouldThrow<AdminException> {
                    adminTemplateService.validateResolvable(
                        templateMeta,
                        listOf(
                            TemplatedRecipient(
                                email = EmailAddress("user1@example.com"),
                                variables = mapOf("name" to "철수", "date" to "2025-01-01"),
                            ),
                        ),
                    )
                }.errorType shouldBe TEMPLATE_VARIABLES_NOT_MATCH
            }
        }

        context("템플릿 치환") {
            test("성공") {
                val template = templateRepository.save(TemplateBuilder().build())

                val actual =
                    adminTemplateService.resolve(
                        listOf(
                            MailMessageInfo(
                                templateId = template.id,
                                recipient = EmailAddress("user1@example.com"),
                                variables = mapOf("name" to "철수"),
                            ),
                        ),
                    )

                actual shouldHaveSize 1
                actual.first() shouldBe
                    MailContent(
                        subject = "철수님 환영합니다.",
                        body =
                            """
                            <!DOCTYPE html>
                            <html lang="ko">
                            <head>
                                <meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
                            </head>
                            <body>
                                <div>철수님 환영합니다.</div>
                            </body>
                            </html>
                            """.trimIndent(),
                    )
            }

            test("템플릿이 없으면 실패한다.") {
                shouldThrow<AdminException> {
                    adminTemplateService.validateResolvable(
                        TemplateMeta(TemplateType("welcome"), 2),
                        listOf(
                            TemplatedRecipient(
                                email = EmailAddress("user1@example.com"),
                                variables = mapOf("name" to "철수"),
                            ),
                        ),
                    )
                }.errorType shouldBe TEMPLATE_NOT_FOUND
            }
        }
    })
