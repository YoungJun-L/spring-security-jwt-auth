package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.api.application.TokenService
import com.youngjun.auth.core.api.controller.v1.request.LoginRequest
import com.youngjun.auth.core.api.support.SecurityTest
import com.youngjun.auth.core.api.support.VALID_PASSWORD
import com.youngjun.auth.core.api.support.VALID_USERNAME
import com.youngjun.auth.core.api.support.description
import com.youngjun.auth.core.api.support.error.ErrorType.AUTH_NOT_FOUND_ERROR
import com.youngjun.auth.core.api.support.ignored
import com.youngjun.auth.core.api.support.type
import com.youngjun.auth.core.domain.auth.AuthBuilder
import com.youngjun.auth.core.domain.auth.AuthStatus
import com.youngjun.auth.core.domain.token.TokenPairBuilder
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
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException

class LoginTest(
    private val userDetailsService: UserDetailsService,
    private val tokenService: TokenService,
) : SecurityTest() {
    @Test
    fun `로그인 성공`() {
        val auth = AuthBuilder(username = VALID_USERNAME).build()
        every { userDetailsService.loadUserByUsername(VALID_USERNAME) } returns auth
        every { tokenService.issue(auth) } returns TokenPairBuilder(authId = auth.id).build()

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(LoginRequest(VALID_USERNAME, VALID_PASSWORD))
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
                        "data.tokens.accessTokenExpiresIn" type NUMBER description "accessToken 만료 시간, UNIX 타임스탬프(Timestamp)",
                        "data.tokens.refreshToken" type STRING description "refreshToken",
                        "data.tokens.refreshTokenExpiresIn" type NUMBER description "refreshToken 만료 시간, UNIX 타임스탬프(Timestamp)",
                        "error" type NULL ignored true,
                    ),
                ),
            )
    }

    @Test
    fun `존재하지 않는 회원이면 실패한다`() {
        val username = "username123"
        val password = "password123!"
        every { userDetailsService.loadUserByUsername(username) } throws UsernameNotFoundException(AUTH_NOT_FOUND_ERROR.message)

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(LoginRequest(username, password))
            .post("/auth/login")
            .then()
            .log()
            .all()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
    }

    @Test
    fun `비밀번호가 다르면 실패한다`() {
        val auth = AuthBuilder(username = VALID_USERNAME).build()
        val password = "invalidPassword123!"
        every { userDetailsService.loadUserByUsername(VALID_USERNAME) } returns auth

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(LoginRequest(VALID_USERNAME, password))
            .post("/auth/login")
            .then()
            .log()
            .all()
            .statusCode(HttpStatus.UNAUTHORIZED.value())
    }

    @Test
    fun `서비스 이용이 제한된 유저이면 실패한다`() {
        val auth = AuthBuilder(username = VALID_USERNAME, status = AuthStatus.DISABLED).build()
        every { userDetailsService.loadUserByUsername(VALID_USERNAME) } returns auth

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .body(LoginRequest(VALID_USERNAME, VALID_PASSWORD))
            .post("/auth/login")
            .then()
            .log()
            .all()
            .statusCode(HttpStatus.FORBIDDEN.value())
    }
}
