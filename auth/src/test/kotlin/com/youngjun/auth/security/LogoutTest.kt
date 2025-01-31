package com.youngjun.auth.security

import com.youngjun.auth.application.AccountService
import com.youngjun.auth.application.TokenService
import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.support.SecurityContextTest
import com.youngjun.auth.support.description
import com.youngjun.auth.support.ignored
import com.youngjun.auth.support.type
import io.mockk.every
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.payload.JsonFieldType.NULL
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.OBJECT
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields

class LogoutTest(
    private val tokenService: TokenService,
    private val accountService: AccountService,
) : SecurityContextTest() {
    @Test
    fun `로그아웃 성공`() {
        val account = AccountBuilder().build()
        every { tokenService.parse(any()) } returns account
        every { accountService.logout(any()) } returns account.logout()

        given()
            .log()
            .all()
            .header(HttpHeaders.AUTHORIZATION, "Bearer a.b.c")
            .post("/auth/logout")
            .then()
            .log()
            .all()
            .statusCode(HttpStatus.OK.value())
            .apply(
                document(
                    "logout",
                    requestHeaders(
                        headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token").optional(),
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
