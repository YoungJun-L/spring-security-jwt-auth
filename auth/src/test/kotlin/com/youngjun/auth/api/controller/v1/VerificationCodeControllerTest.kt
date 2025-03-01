package com.youngjun.auth.api.controller.v1

import com.youngjun.auth.api.controller.v1.request.SendVerificationCodeRequest
import com.youngjun.auth.application.MailService
import com.youngjun.auth.application.VerificationCodeService
import com.youngjun.auth.domain.account.EmailAddressBuilder
import com.youngjun.auth.domain.verificationCode.generateVerificationCode
import com.youngjun.test.RestDocsTest
import com.youngjun.test.description
import com.youngjun.test.ignored
import com.youngjun.test.type
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.restassured.http.ContentType
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.JsonFieldType.NULL
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields

class VerificationCodeControllerTest : RestDocsTest() {
    private lateinit var verificationCodeService: VerificationCodeService
    private lateinit var mailService: MailService

    @BeforeEach
    fun setUp() {
        verificationCodeService = mockk()
        mailService = mockk()
        val verificationCodeController = VerificationCodeController(verificationCodeService, mailService)
        setMockMvc(verificationCodeController)
    }

    @Test
    fun `인증 코드 전송 성공`() {
        val emailAddress = EmailAddressBuilder().build()
        every { verificationCodeService.generate(emailAddress, any()) } returns generateVerificationCode(emailAddress)
        every { mailService.sendVerificationCode(any()) } just Runs

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(SendVerificationCodeRequest(emailAddress))
            .post("/auth/send-verification-code")
            .then()
            .log()
            .all()
            .apply(
                document(
                    "send-verification-code",
                    requestFields(
                        "email" type STRING description "email, 이메일 형식",
                    ),
                    responseFields(
                        "status" type STRING description "status",
                        "data" type NULL description "data",
                        "error" type NULL ignored true,
                    ),
                ),
            )
    }
}
