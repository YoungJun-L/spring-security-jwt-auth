package com.youngjun.auth.security

import com.youngjun.auth.application.TokenService
import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.domain.token.RawAccessToken
import com.youngjun.auth.support.SecurityContextTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorCode
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DISABLED
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_LOGOUT
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_NOT_FOUND
import com.youngjun.auth.support.error.ErrorType.TOKEN_EXPIRED
import com.youngjun.auth.support.error.ErrorType.TOKEN_INVALID
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.restassured.http.ContentType
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document

class JwtAuthenticationTest(
    private val tokenService: TokenService,
) : SecurityContextTest() {
    @Test
    fun `JWT 인증 성공`() {
        val accessToken = RawAccessToken("a.b.c")
        every { tokenService.parse(accessToken) } returns AccountBuilder().build()

        given()
            .log()
            .all()
            .header(HttpHeaders.AUTHORIZATION, "Bearer ${accessToken.value}")
            .contentType(ContentType.JSON)
            .get("/test")
            .then()
            .log()
            .all()
            .statusCode(HttpStatus.OK.value())
            .apply(
                document(
                    "authenticate",
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token").optional(),
                    ),
                ),
            )
    }

    @Test
    fun `유효하지 않은 JWT 이면 실패한다`() {
        every { tokenService.parse(any()) } throws AuthException(TOKEN_INVALID)

        val actual = authenticate()

        actual["code"] shouldBe ErrorCode.E4012.name
    }

    @Test
    fun `만료된 JWT 이면 실패한다`() {
        every { tokenService.parse(any()) } throws AuthException(TOKEN_EXPIRED)

        val actual = authenticate()

        actual["code"] shouldBe ErrorCode.E4013.name
    }

    @Test
    fun `존재하지 않는 유저이면 실패한다`() {
        every { tokenService.parse(any()) } throws AuthException(ACCOUNT_NOT_FOUND)

        val actual = authenticate()

        actual["code"] shouldBe ErrorCode.E4041.name
    }

    @Test
    fun `서비스 이용이 제한된 유저이면 실패한다`() {
        every { tokenService.parse(any()) } throws AuthException(ACCOUNT_DISABLED)

        val actual = authenticate()

        actual["code"] shouldBe ErrorCode.E4032.name
    }

    @Test
    fun `로그아웃된 유저이면 실패한다`() {
        every { tokenService.parse(any()) } throws AuthException(ACCOUNT_LOGOUT)

        val actual = authenticate()

        actual["code"] shouldBe ErrorCode.E4033.name
    }
}

private fun authenticate(accessToken: String = "a.b.c"): Map<String, String> =
    given()
        .log()
        .all()
        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
        .contentType(ContentType.JSON)
        .get("/test")
        .then()
        .log()
        .all()
        .extract()
        .jsonPath()
        .getMap("error")
