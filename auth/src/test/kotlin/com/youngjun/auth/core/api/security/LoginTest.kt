package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.api.application.AccountService
import com.youngjun.auth.core.api.application.TokenService
import com.youngjun.auth.core.api.controller.v1.request.LoginRequest
import com.youngjun.auth.core.domain.account.AccountBuilder
import com.youngjun.auth.core.domain.account.AccountStatus
import com.youngjun.auth.core.domain.token.TokenPairDetailsBuilder
import com.youngjun.auth.core.support.SecurityContextTest
import com.youngjun.auth.core.support.description
import com.youngjun.auth.core.support.error.ErrorCode
import com.youngjun.auth.core.support.ignored
import com.youngjun.auth.core.support.type
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.restassured.http.ContentType
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.Test
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.JsonFieldType.NULL
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.OBJECT
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder

class LoginTest(
    private val accountService: AccountService,
    private val tokenService: TokenService,
    private val passwordEncoder: PasswordEncoder,
) : SecurityContextTest() {
    @Test
    fun `로그인 성공`() {
        val username = "username123"
        val password = "password123!"
        val account = AccountBuilder(username = username, password = passwordEncoder.encode(password)).build()
        every { accountService.loadUserByUsername(any()) } returns account
        every { tokenService.issue(any()) } returns TokenPairDetailsBuilder(userId = account.id).build()

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(LoginRequest(username, password))
            .post("/auth/login")
            .then()
            .log()
            .all()
            .apply(
                document(
                    "login",
                    requestFields(
                        "username" type STRING description "username",
                        "password" type STRING description "password",
                    ),
                    responseFields(
                        "status" type STRING description "status",
                        "data" type OBJECT description "data",
                        "data.userId" type NUMBER description "발급 userId",
                        "data.tokens" type OBJECT description "tokens",
                        "data.tokens.accessToken" type STRING description "accessToken",
                        "data.tokens.accessTokenExpiration" type NUMBER description "accessToken 만료 시간, UNIX 타임스탬프(Timestamp)",
                        "data.tokens.refreshToken" type STRING description "refreshToken",
                        "data.tokens.refreshTokenExpiration" type NUMBER description "refreshToken 만료 시간, UNIX 타임스탬프(Timestamp)",
                        "error" type NULL ignored true,
                    ),
                ),
            )
    }

    @Test
    fun `존재하지 않는 회원이면 실패한다`() {
        every { accountService.loadUserByUsername(any()) } throws UsernameNotFoundException("")

        val actual = login("username123", "password123!")

        actual["code"] shouldBe ErrorCode.E4011.name
    }

    @Test
    fun `비밀번호가 다르면 실패한다`() {
        val username = "username123"
        val password = "password123!"
        every { accountService.loadUserByUsername(any()) } returns
            AccountBuilder(
                username = username,
                password = passwordEncoder.encode(password),
            ).build()

        val actual = login(username, "invalidPassword123!")

        actual["code"] shouldBe ErrorCode.E4011.name
    }

    @Test
    fun `서비스 이용이 제한된 유저이면 실패한다`() {
        val username = "username123"
        val password = "password123!"
        every { accountService.loadUserByUsername(any()) } returns
            AccountBuilder(
                username = username,
                password = passwordEncoder.encode(password),
                status = AccountStatus.DISABLED,
            ).build()

        val actual = login(username, password)

        actual["code"] shouldBe ErrorCode.E4032.name
    }
}

private fun login(
    username: String,
    password: String,
): Map<String, String> =
    given()
        .log()
        .all()
        .contentType(ContentType.JSON)
        .body(LoginRequest(username, password))
        .post("/auth/login")
        .then()
        .log()
        .all()
        .extract()
        .jsonPath()
        .getMap("error")
