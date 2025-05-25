package com.youngjun.admin.api.controller.v1

import com.youngjun.admin.application.AdminTemplateService
import com.youngjun.admin.domain.template.TemplateBuilder
import com.youngjun.tests.RestDocsTest
import com.youngjun.tests.description
import com.youngjun.tests.type
import io.mockk.every
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
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields

class AdminTemplateControllerTest : RestDocsTest() {
    private lateinit var adminTemplateService: AdminTemplateService

    @BeforeEach
    fun setUp() {
        adminTemplateService = mockk()
        val adminTemplateController = AdminTemplateController(adminTemplateService)
        setMockMvc(adminTemplateController)
    }

    @Test
    fun `템플릿 목록 조회 성공`() {
        every { adminTemplateService.findAll() } returns listOf(TemplateBuilder().build())

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .get("/templates")
            .then()
            .log()
            .all()
            .status(HttpStatus.OK)
            .apply(
                document(
                    "templates",
                    responseFields(
                        "templates" type ARRAY description "템플릿 목록",
                        "templates[].templateType" type STRING description "템플릿 메타 정보",
                        "templates[].version" type NUMBER description "템플릿 버전",
                        "templates[].subject" type STRING description "메일 제목",
                        "templates[].body" type STRING description "메일 본문 html",
                        "templates[].variables" type OBJECT description "템플릿 변수",
                        "templates[].variables.name" type STRING description "템플릿 변수 키",
                    ),
                ),
            )
    }
}
