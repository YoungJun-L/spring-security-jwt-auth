package com.youngjun.admin.api.controller.v1

import com.youngjun.admin.api.controller.v1.request.SendMailRequest
import com.youngjun.admin.application.AdminMailService
import com.youngjun.admin.application.AdminTemplateService
import com.youngjun.admin.domain.template.TemplateMeta
import com.youngjun.admin.domain.template.TemplateType
import com.youngjun.admin.domain.template.TemplatedRecipient
import com.youngjun.admin.domain.user.EmailAddress
import com.youngjun.tests.RestDocsTest
import com.youngjun.tests.description
import com.youngjun.tests.type
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.restassured.http.ContentType
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.JsonFieldType.ARRAY
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.OBJECT
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields

class AdminMailControllerTest : RestDocsTest() {
    private lateinit var adminMailService: AdminMailService
    private lateinit var adminTemplateService: AdminTemplateService

    @BeforeEach
    fun setUp() {
        adminMailService = mockk()
        adminTemplateService = mockk()
        val adminMailController = AdminMailController(adminMailService, adminTemplateService)
        setMockMvc(adminMailController)
    }

    @Test
    fun `메일 전송 성공`() {
        val templatedRecipients =
            listOf(
                TemplatedRecipient(EmailAddress("a@gmail.com"), mapOf("name" to "A")),
                TemplatedRecipient(EmailAddress("b@gmail.com"), mapOf("name" to "B")),
            )
        every { adminTemplateService.validateResolvable(any(), any()) } returns 1L
        every { adminMailService.submit(any(), any()) } just Runs

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(
                SendMailRequest(
                    TemplateMeta(TemplateType("welcome"), 1),
                    templatedRecipients,
                ),
            ).post("/emails/send")
            .then()
            .log()
            .all()
            .status(HttpStatus.ACCEPTED)
            .apply(
                document(
                    "emails-send",
                    requestFields(
                        "template" type OBJECT description "템플릿 메타 정보",
                        "template.type" type STRING description "템플릿 타입",
                        "template.version" type NUMBER description "템플릿 버전",
                        "recipients" type ARRAY description "수신자 이메일 목록",
                        "recipients[].email" type STRING description "수신자 이메일",
                        "recipients[].variables" type OBJECT description "템플릿 변수",
                        "recipients[].variables.name" type STRING description "템플릿 변수 키",
                    ),
                ),
            )
    }
}
