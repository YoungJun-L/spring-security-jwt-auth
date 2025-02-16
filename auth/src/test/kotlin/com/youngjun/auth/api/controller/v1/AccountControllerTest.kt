package com.youngjun.auth.api.controller.v1

import com.youngjun.auth.api.controller.v1.request.ChangePasswordRequest
import com.youngjun.auth.api.controller.v1.request.RegisterAccountRequest
import com.youngjun.auth.application.AccountService
import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.security.token.JwtAuthenticationToken
import com.youngjun.auth.support.RestDocsTest
import com.youngjun.auth.support.description
import com.youngjun.auth.support.ignored
import com.youngjun.auth.support.type
import io.mockk.every
import io.mockk.mockk
import io.restassured.http.ContentType
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.JsonFieldType.NULL
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.OBJECT
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.security.core.context.SecurityContextHolder

class AccountControllerTest : RestDocsTest() {
    private lateinit var accountService: AccountService

    @BeforeEach
    fun setUp() {
        accountService = mockk()
        val accountController = AccountController(accountService)
        setMockMvc(accountController)
    }

    @Test
    fun `회원가입 성공`() {
        every { accountService.register(any(), any()) } returns AccountBuilder().build()

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(RegisterAccountRequest("example@youngjun.com", "password123!"))
            .post("/auth/register")
            .then()
            .log()
            .all()
            .apply(
                document(
                    "register",
                    requestFields(
                        "email" type STRING description "email, 이메일 형식",
                        "password" type STRING description "password, 최소 8자 이상 최대 65자 미만",
                    ),
                    responseFields(
                        "status" type STRING description "status",
                        "data" type OBJECT description "data",
                        "data.userId" type NUMBER description "userId",
                        "error" type NULL ignored true,
                    ),
                ),
            )
    }

    @Test
    fun `비밀번호 변경 성공`() {
        val account = AccountBuilder().build()
        SecurityContextHolder.getContext().authentication = JwtAuthenticationToken.authenticated(account)
        every { accountService.changePassword(any(), any()) } returns account

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(ChangePasswordRequest("password123!"))
            .put("/account/password")
            .then()
            .log()
            .all()
            .apply(
                document(
                    "change-password",
                    requestFields(
                        "password" type STRING description "password, 최소 8자 이상 최대 65자 미만",
                    ),
                    responseFields(
                        "status" type STRING description "status",
                        "data" type OBJECT description "data",
                        "data.userId" type NUMBER description "userId",
                        "error" type NULL ignored true,
                    ),
                ),
            )
    }
}
