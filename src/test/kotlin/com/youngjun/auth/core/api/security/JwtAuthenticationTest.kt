package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.api.support.SecurityTest
import com.youngjun.auth.core.domain.auth.AuthBuilder
import com.youngjun.auth.core.domain.auth.AuthService
import com.youngjun.auth.core.domain.token.TokenParser
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
    private val tokenParser: TokenParser,
    private val authService: AuthService,
) : SecurityTest() {
    @Test
    fun `JWT 인증 성공`() {
        val value = "a.b.c"
        val username = "username123"
        every { tokenParser.parseSubject(value) } returns username
        every { authService.loadUserByUsername(username) } returns AuthBuilder().build()

        given()
            .log()
            .all()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $value")
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
}
