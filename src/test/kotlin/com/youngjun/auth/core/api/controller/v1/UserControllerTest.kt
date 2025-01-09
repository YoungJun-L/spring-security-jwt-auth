package com.youngjun.auth.core.api.controller.v1

import com.youngjun.auth.core.api.application.UserService
import com.youngjun.auth.core.api.controller.v1.request.RegisterUserRequest
import com.youngjun.auth.core.api.support.RestDocsTest
import com.youngjun.auth.core.api.support.description
import com.youngjun.auth.core.api.support.ignored
import com.youngjun.auth.core.api.support.type
import com.youngjun.auth.core.domain.user.NewUserBuilder
import com.youngjun.auth.core.domain.user.UserBuilder
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

class UserControllerTest : RestDocsTest() {
    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userService = mockk()
        val userController = UserController(userService)
        setMockMvc(userController)
    }

    @Test
    fun `회원가입 성공`() {
        val validUsername = "username123"
        val validPassword = "password123!"
        every {
            userService.register(
                NewUserBuilder(
                    validUsername,
                    validPassword,
                ).build(),
            )
        } returns UserBuilder().build()

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(
                RegisterUserRequest(validUsername, validPassword),
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
