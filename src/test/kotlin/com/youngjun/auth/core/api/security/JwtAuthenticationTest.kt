package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.api.support.SecurityTest
import com.youngjun.auth.core.api.support.VALID_USERNAME
import com.youngjun.auth.core.api.support.error.ErrorType.USER_NOT_FOUND_ERROR
import com.youngjun.auth.core.domain.token.TokenParser
import com.youngjun.auth.core.domain.user.UserBuilder
import com.youngjun.auth.core.domain.user.UserStatus
import io.mockk.every
import io.restassured.http.ContentType
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class JwtAuthenticationTest(
    private val tokenParser: TokenParser,
    private val userDetailsService: UserDetailsService,
) : SecurityTest() {
    @Test
    fun `JWT 인증 성공`() {
        val value = "a.b.c"
        val username = "username123"
        every { tokenParser.parseSubject(value) } returns username
        every { userDetailsService.loadUserByUsername(username) } returns UserBuilder(username = username).build()

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

    @Test
    fun `유효하지 않은 JWT 이면 실패한다`() {
        val value = "a.b.c"
        every { tokenParser.parseSubject(value) } throws BadCredentialsException("Invalid token")

        given()
            .log()
            .all()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $value")
            .contentType(ContentType.JSON)
            .get("/test")
            .then()
            .log()
            .all()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
    }

    @Test
    fun `존재하지 않는 회원이면 실패한다`() {
        val value = "a.b.c"
        val username = "username123"
        every { tokenParser.parseSubject(value) } returns username
        every { userDetailsService.loadUserByUsername(username) } throws UsernameNotFoundException(USER_NOT_FOUND_ERROR.message)

        given()
            .log()
            .all()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $value")
            .contentType(ContentType.JSON)
            .get("/test")
            .then()
            .log()
            .all()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
    }

    @Test
    fun `서비스 이용이 제한된 유저이면 실패한다`() {
        val value = "a.b.c"
        val user = UserBuilder(username = VALID_USERNAME, status = UserStatus.DISABLED).build()
        every { tokenParser.parseSubject(value) } returns VALID_USERNAME
        every { userDetailsService.loadUserByUsername(VALID_USERNAME) } returns user

        given()
            .log()
            .all()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $value")
            .contentType(ContentType.JSON)
            .get("/test")
            .then()
            .log()
            .all()
            .statusCode(HttpStatus.FORBIDDEN.value())
    }
}
