package com.youngjun.auth.api.controller.v1

import com.youngjun.auth.api.config.AccountArgumentResolver
import com.youngjun.auth.api.controller.v1.request.ChangePasswordRequest
import com.youngjun.auth.api.controller.v1.request.ProfileRequest
import com.youngjun.auth.api.controller.v1.request.RegisterAccountRequest
import com.youngjun.auth.application.AccountService
import com.youngjun.auth.application.PasswordService
import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.domain.account.EMAIL_ADDRESS
import com.youngjun.auth.domain.account.PROFILE
import com.youngjun.auth.domain.account.RAW_PASSWORD
import com.youngjun.auth.domain.account.RawPassword
import com.youngjun.auth.domain.token.TokenPairBuilder
import com.youngjun.auth.domain.verificationCode.RAW_VERIFICATION_CODE
import com.youngjun.auth.security.token.JwtAuthenticationToken
import com.youngjun.tests.RestDocsTest
import com.youngjun.tests.description
import com.youngjun.tests.ignored
import com.youngjun.tests.type
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
        every {
            accountService.register(
                emailAddress = EMAIL_ADDRESS,
                rawPassword = RAW_PASSWORD,
                profile = PROFILE,
                rawVerificationCode = RAW_VERIFICATION_CODE,
                any(),
            )
        } returns
            AccountBuilder(emailAddress = EMAIL_ADDRESS).build()

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(
                RegisterAccountRequest(
                    EMAIL_ADDRESS,
                    RAW_PASSWORD,
                    RAW_VERIFICATION_CODE,
                    ProfileRequest(
                        name = PROFILE.name,
                        nickname = PROFILE.nickname,
                        phoneNumber = PROFILE.phoneNumber,
                        country = PROFILE.country,
                    ),
                ),
            ).post("/auth/register")
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
                        "profile" type OBJECT description "프로필 정보",
                        "profile.name" type STRING description "이름",
                        "profile.nickname" type STRING description "별명",
                        "profile.phoneNumber" type STRING description "전화번호",
                        "profile.country" type STRING description "국가 코드",
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
