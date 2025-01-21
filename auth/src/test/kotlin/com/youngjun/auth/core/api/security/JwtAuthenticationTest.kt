package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.api.application.AccountService
import com.youngjun.auth.core.domain.account.AccountBuilder
import com.youngjun.auth.core.domain.account.AccountStatus
import com.youngjun.auth.core.domain.token.TokenProvider
import com.youngjun.auth.core.support.SecurityTest
import com.youngjun.auth.core.support.VALID_USERNAME
import com.youngjun.auth.core.support.error.ErrorCode
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
import org.springframework.security.authentication.CredentialsExpiredException
import org.springframework.security.core.userdetails.UsernameNotFoundException

class JwtAuthenticationTest(
    private val accountService: AccountService,
    private val tokenProvider: TokenProvider,
) : SecurityTest() {
    @Test
    fun `JWT 인증 성공`() {
        val username = "username123"
        every { tokenProvider.parseSubject(any()) } returns username
        every { accountService.loadUserByUsername(any()) } returns AccountBuilder(username = username).build()

        given()
            .log()
            .all()
            .header(HttpHeaders.AUTHORIZATION, "Bearer a.b.c")
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
        every { tokenProvider.parseSubject(any()) } throws InvalidTokenException("")

        val actual = authenticate()

        actual["code"] shouldBe ErrorCode.E4012.name
    }

    @Test
    fun `만료된 JWT 이면 실패한다`() {
        every { tokenProvider.parseSubject(any()) } throws CredentialsExpiredException("")

        val actual = authenticate()

        actual["code"] shouldBe ErrorCode.E4013.name
    }

    @Test
    fun `존재하지 않는 회원이면 실패한다`() {
        every { tokenProvider.parseSubject(any()) } returns "username123"
        every { accountService.loadUserByUsername(any()) } throws UsernameNotFoundException("")

        val actual = authenticate()

        actual["code"] shouldBe ErrorCode.E4010.name
    }

    @Test
    fun `서비스 이용이 제한된 유저이면 실패한다`() {
        every { tokenProvider.parseSubject(any()) } returns VALID_USERNAME
        every { accountService.loadUserByUsername(any()) } returns
            AccountBuilder(
                username = VALID_USERNAME,
                status = AccountStatus.DISABLED,
            ).build()

        val actual = authenticate()

        actual["code"] shouldBe ErrorCode.E4032.name
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
