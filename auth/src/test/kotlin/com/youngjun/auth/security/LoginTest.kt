package com.youngjun.auth.security

import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.domain.account.AccountRepository
import com.youngjun.auth.domain.account.AccountStatus
import com.youngjun.auth.domain.account.EMAIL_ADDRESS
import com.youngjun.auth.domain.account.EmailAddress
import com.youngjun.auth.domain.account.Password
import com.youngjun.auth.domain.account.RAW_PASSWORD
import com.youngjun.auth.domain.account.RawPassword
import com.youngjun.auth.security.filter.LoginRequest
import com.youngjun.auth.support.SecurityContextTest
import com.youngjun.auth.support.error.ErrorCode
import com.youngjun.tests.description
import com.youngjun.tests.ignored
import com.youngjun.tests.type
import io.kotest.matchers.shouldBe
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
import org.springframework.security.crypto.password.PasswordEncoder

class LoginTest(
    private val passwordEncoder: PasswordEncoder,
    private val accountRepository: AccountRepository,
) : SecurityContextTest() {
    @Test
    fun `로그인 성공`() {
        accountRepository.save(AccountBuilder(EMAIL_ADDRESS, Password.encodedWith(RAW_PASSWORD, passwordEncoder)).build())

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(LoginRequest(EMAIL_ADDRESS, RAW_PASSWORD))
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
        val actual = login(EMAIL_ADDRESS, RAW_PASSWORD)

        actual["code"] shouldBe ErrorCode.E4011.name
    }

    @Test
    fun `비밀번호가 다르면 실패한다`() {
        accountRepository.save(
            AccountBuilder(emailAddress = EMAIL_ADDRESS, password = Password.encodedWith(RAW_PASSWORD, passwordEncoder)).build(),
        )

        val actual = login(EMAIL_ADDRESS, RawPassword(value = "wrongPassword"))

        actual["code"] shouldBe ErrorCode.E4011.name
    }

    @Test
    fun `서비스 이용이 제한된 유저이면 실패한다`() {
        accountRepository.save(
            AccountBuilder(EMAIL_ADDRESS, Password.encodedWith(RAW_PASSWORD, passwordEncoder), AccountStatus.DISABLED).build(),
        )

        val actual = login(EMAIL_ADDRESS, RAW_PASSWORD)

        actual["code"] shouldBe ErrorCode.E4032.name
    }

    @Test
    fun `로그아웃된 유저이면 성공한다`() {
        accountRepository.save(
            AccountBuilder(EMAIL_ADDRESS, Password.encodedWith(RAW_PASSWORD, passwordEncoder), AccountStatus.LOGOUT).build(),
        )

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(LoginRequest(EMAIL_ADDRESS, RAW_PASSWORD))
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
        .body(LoginRequest(emailAddress, rawPassword))
        .post("/auth/login")
        .then()
        .log()
        .all()
        .extract()
        .jsonPath()
        .getMap("error")
