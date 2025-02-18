package com.youngjun.auth.security

import com.youngjun.auth.application.AccountService
import com.youngjun.auth.application.TokenService
import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.domain.account.AccountStatus
import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.account.EmailAddressBuilder
import com.youngjun.auth.domain.account.Password
import com.youngjun.auth.domain.account.PasswordBuilder
import com.youngjun.auth.domain.account.RawPassword
import com.youngjun.auth.domain.account.RawPasswordBuilder
import com.youngjun.auth.domain.token.TokenPairBuilder
import com.youngjun.auth.security.filter.LoginRequest
import com.youngjun.auth.support.SecurityContextTest
import com.youngjun.auth.support.description
import com.youngjun.auth.support.error.ErrorCode
import com.youngjun.auth.support.ignored
import com.youngjun.auth.support.type
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.restassured.http.ContentType
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
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
        val emailAddress = EmailAddressBuilder().build()
        val rawPassword = RawPasswordBuilder().build()
        val account = AccountBuilder(emailAddress, Password.encodedWith(rawPassword, passwordEncoder)).build()
        every { accountService.loadUserByUsername(any()) } returns account
        every { tokenService.issue(any()) } returns TokenPairBuilder(userId = account.id).build()
        every { accountService.login(any()) } returns account.apply { account.enable() }

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(LoginRequest(emailAddress.value, rawPassword.value))
            .post("/auth/login")
            .then()
            .log()
            .all()
            .apply(
                document(
                    "login",
                    requestFields(
                        "email" type STRING description "email",
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
    fun `존재하지 않는 유저이면 실패한다`() {
        every { accountService.loadUserByUsername(any()) } throws UsernameNotFoundException("")

        val actual = login(EmailAddressBuilder().build(), RawPasswordBuilder().build())

        actual["code"] shouldBe ErrorCode.E4011.name
    }

    @Test
    fun `비밀번호가 다르면 실패한다`() {
        val emailAddress = EmailAddressBuilder().build()
        val password = PasswordBuilder().build()
        every { accountService.loadUserByUsername(any()) } returns AccountBuilder(emailAddress = emailAddress, password = password).build()

        val actual = login(emailAddress, RawPasswordBuilder(value = "invalidPassword123!").build())

        actual["code"] shouldBe ErrorCode.E4011.name
    }

    @Test
    fun `서비스 이용이 제한된 유저이면 실패한다`() {
        val emailAddress = EmailAddressBuilder().build()
        val rawPassword = RawPasswordBuilder().build()
        every { accountService.loadUserByUsername(any()) } returns
            AccountBuilder(
                emailAddress = emailAddress,
                password = Password.encodedWith(rawPassword, passwordEncoder),
                status = AccountStatus.DISABLED,
            ).build()

        val actual = login(emailAddress, rawPassword)

        actual["code"] shouldBe ErrorCode.E4032.name
    }

    @Test
    fun `로그아웃된 유저이면 성공한다`() {
        val emailAddress = EmailAddressBuilder().build()
        val rawPassword = RawPasswordBuilder().build()
        val account = AccountBuilder(emailAddress, Password.encodedWith(rawPassword, passwordEncoder), AccountStatus.LOGOUT).build()
        every { accountService.loadUserByUsername(any()) } returns account
        every { tokenService.issue(any()) } returns TokenPairBuilder(userId = account.id).build()
        every { accountService.login(any()) } returns account.apply { enable() }

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(LoginRequest(emailAddress.value, rawPassword.value))
            .post("/auth/login")
            .then()
            .log()
            .all()
            .statusCode(HttpStatus.OK.value())
    }
}

private fun login(
    emailAddress: EmailAddress,
    rawPassword: RawPassword,
): Map<String, String> =
    given()
        .log()
        .all()
        .contentType(ContentType.JSON)
        .body(LoginRequest(emailAddress.value, rawPassword.value))
        .post("/auth/login")
        .then()
        .log()
        .all()
        .extract()
        .jsonPath()
        .getMap("error")
