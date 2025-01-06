package com.youngjun.auth.core.api.controller.v1

import com.youngjun.auth.core.api.application.AuthService
import com.youngjun.auth.core.api.controller.v1.request.RegisterAuthRequest
import com.youngjun.auth.core.api.support.RestDocsTest
import com.youngjun.auth.core.api.support.description
import com.youngjun.auth.core.api.support.ignored
import com.youngjun.auth.core.api.support.type
import com.youngjun.auth.core.domain.auth.AuthBuilder
import com.youngjun.auth.core.domain.auth.NewAuthBuilder
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

class AuthControllerTest : RestDocsTest() {
    private lateinit var authService: AuthService

    @BeforeEach
    fun setUp() {
        authService = mockk()
        val authController = AuthController(authService)
        setMockMvc(authController)
    }

    @Test
    fun `회원가입 성공`() {
        val validUsername = "username123"
        val validPassword = "password123!"
        every {
            authService.register(
                NewAuthBuilder(
                    validUsername,
                    validPassword,
                ).build(),
            )
        } returns AuthBuilder().build()

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(
                RegisterAuthRequest(validUsername, validPassword),
            ).post("/auth/register")
            .then()
            .log()
            .all()
            .apply(
                document(
                    "register",
                    requestFields(
                        "username" type STRING description "username, 최소 8자 이상 최대 50자 미만의 1개 이상 영문자, 1개 이상 숫자 조합",
                        "password" type STRING description "password, 최소 10자 이상 최대 50자 미만의 1개 이상 영문자, 1개 이상 특수문자, 1개 이상 숫자 조합",
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
