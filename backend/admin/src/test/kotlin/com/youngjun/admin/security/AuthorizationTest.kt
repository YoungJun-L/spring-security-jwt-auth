package com.youngjun.admin.security

import com.youngjun.admin.domain.AdministratorBuilder
import com.youngjun.admin.support.SecurityContextTest
import io.restassured.http.ContentType
import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder

class AuthorizationTest : SecurityContextTest() {
    @AfterEach
    fun tearDown() {
        SecurityContextHolder.clearContext()
    }

    @Test
    fun `접근 성공`() {
        val administrator = AdministratorBuilder().build()
        SecurityContextHolder.getContext().authentication =
            UsernamePasswordAuthenticationToken(
                administrator.username,
                administrator.password,
                administrator.authorities,
            )

        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .get("/test")
            .then()
            .log()
            .all()
            .status(HttpStatus.OK)
    }

    @Test
    fun `승인되지 않은 관리자면 실패한다`() {
        given()
            .log()
            .all()
            .contentType(ContentType.JSON)
            .get("/test")
            .then()
            .log()
            .all()
            .status(HttpStatus.FOUND)
    }
}
