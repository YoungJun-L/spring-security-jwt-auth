package com.youngjun.auth.api.controller.v1

import com.youngjun.auth.api.config.AccountArgumentResolver
import com.youngjun.auth.api.controller.v1.request.ChangePasswordRequest
import com.youngjun.auth.api.controller.v1.request.RegisterAccountRequest
import com.youngjun.auth.application.AccountService
import com.youngjun.auth.application.PasswordService
import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.domain.account.EmailAddressBuilder
import com.youngjun.auth.domain.account.RawPassword
import com.youngjun.auth.domain.token.TokenPairBuilder
import com.youngjun.auth.domain.verificationCode.RawVerificationCode
import com.youngjun.auth.security.token.JwtAuthenticationToken
import com.youngjun.test.RestDocsTest
import com.youngjun.test.description
import com.youngjun.test.ignored
import com.youngjun.test.type
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
    private lateinit var passwordService: PasswordService

    @BeforeEach
    fun setUp() {
        accountService = mockk()
        passwordService = mockk()
        val accountController = AccountController(accountService, passwordService)
        setMockMvc(accountController, AccountArgumentResolver)
    }

    @Test
    fun `회원가입 성공`() {
        val emailAddress = EmailAddressBuilder().build()
        val rawPassword = RawPassword("password123!")
        val rawVerificationCode = RawVerificationCode("012345")
        every { accountService.register(emailAddress, rawPassword, rawVerificationCode, any()) } returns
            AccountBuilder(emailAddress = emailAddress).build()

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(RegisterAccountRequest(emailAddress, rawPassword, rawVerificationCode))
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
                        "verificationCode" type STRING description "인증 코드 6자리 숫자",
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
        every { passwordService.changePassword(any(), any(), any(), any()) } returns TokenPairBuilder(userId = account.id).build()

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(ChangePasswordRequest(RawPassword("oldPassword"), RawPassword("newPassword")))
            .put("/account/password")
            .then()
            .log()
            .all()
            .apply(
                document(
                    "change-password",
                    requestFields(
                        "oldPassword" type STRING description "현재 비밀번호, 최소 8자 이상 최대 65자 미만",
                        "newPassword" type STRING description "변경하려는 비밀번호, 최소 8자 이상 최대 65자 미만",
                    ),
                    responseFields(
                        "status" type STRING description "status",
                        "data" type OBJECT description "data",
                        "data.userId" type NUMBER description "userId",
                        "data.tokens.accessToken" type STRING description "accessToken",
                        "data.tokens.accessTokenExpiration" type NUMBER description "accessToken 만료 시간, UNIX 타임스탬프(Timestamp)",
                        "data.tokens.refreshToken" type STRING description "refreshToken",
                        "data.tokens.refreshTokenExpiration" type NUMBER description "refreshToken 만료 시간, UNIX 타임스탬프(Timestamp)",
                        "error" type NULL ignored true,
                    ),
                ),
            )
    }
}
