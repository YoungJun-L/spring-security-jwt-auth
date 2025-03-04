package com.youngjun.auth.security

import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.domain.account.AccountRepository
import com.youngjun.auth.domain.account.AccountStatus
import com.youngjun.auth.infra.jwt.JwtBuilder
import com.youngjun.auth.infra.jwt.JwtProperties
import com.youngjun.auth.support.SecurityContextTest
import com.youngjun.auth.support.error.ErrorCode.E4010
import com.youngjun.auth.support.error.ErrorCode.E4012
import com.youngjun.auth.support.error.ErrorCode.E4013
import com.youngjun.auth.support.error.ErrorCode.E4032
import com.youngjun.auth.support.error.ErrorCode.E4033
import io.kotest.matchers.shouldBe
import io.restassured.http.ContentType
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import java.time.Duration

class JwtAuthenticationTest(
    private val accountRepository: AccountRepository,
    private val jwtProperties: JwtProperties,
) : SecurityContextTest() {
    @Test
    fun `JWT 인증 성공`() {
        val account = accountRepository.save(AccountBuilder().build())
        val accessToken = JwtBuilder(subject = account.id.toString(), secretKey = jwtProperties.accessSecretKey).build()

        given()
            .log()
            .all()
            .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
            .contentType(ContentType.JSON)
            .get("/test")
            .then()
            .log()
            .all()
            .status(HttpStatus.OK)
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
        val actual = authenticate()

        actual["code"] shouldBe E4012.name
    }

    @Test
    fun `만료된 JWT 이면 실패한다`() {
        val account = accountRepository.save(AccountBuilder().build())
        val accessToken =
            JwtBuilder(subject = account.id.toString(), expiresIn = Duration.ZERO, secretKey = jwtProperties.accessSecretKey).build()

        val actual = authenticate(accessToken)

        actual["code"] shouldBe E4013.name
    }

    @Test
    fun `존재하지 않는 유저이면 실패한다`() {
        val actual = authenticate(JwtBuilder(subject = "1", secretKey = jwtProperties.accessSecretKey).build())

        actual["code"] shouldBe E4010.name
    }

    @Test
    fun `서비스 이용이 제한된 유저이면 실패한다`() {
        val account = accountRepository.save(AccountBuilder(status = AccountStatus.DISABLED).build())
        val accessToken = JwtBuilder(subject = account.id.toString(), secretKey = jwtProperties.accessSecretKey).build()

        val actual = authenticate(accessToken)

        actual["code"] shouldBe E4032.name
    }

    @Test
    fun `로그아웃된 유저이면 실패한다`() {
        val account = accountRepository.save(AccountBuilder(status = AccountStatus.LOGOUT).build())
        val accessToken = JwtBuilder(subject = account.id.toString(), secretKey = jwtProperties.accessSecretKey).build()

        val actual = authenticate(accessToken)

        actual["code"] shouldBe E4033.name
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
