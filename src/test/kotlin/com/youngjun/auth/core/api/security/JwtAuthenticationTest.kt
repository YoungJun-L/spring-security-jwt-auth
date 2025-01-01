package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.api.support.AcceptanceTest
import com.youngjun.auth.core.api.support.context
import com.youngjun.auth.core.api.support.document
import com.youngjun.auth.core.api.support.response.AuthResponse
import com.youngjun.auth.core.domain.token.TokenParser
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@AcceptanceTest
class JwtAuthenticationTest(
    private val tokenParser: TokenParser,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("JWT 인증", listOf("/acceptance/auth.json")) {
                test("성공") {
                    val accessToken = "a.b.c"
                    val username = "username123"
                    every { tokenParser.parseSubject(accessToken) } returns username

                    given()
                        .log()
                        .all()
                        .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                        .contentType(ContentType.JSON)
                        .document(
                            "authenticate",
                            requestHeaders(
                                headerWithName(HttpHeaders.AUTHORIZATION).description("JWT access token").optional(),
                            ),
                        ).get("/test")
                        .then()
                        .log()
                        .all()
                        .statusCode(HttpStatus.OK.value())
                }

                test("유효하지 않은 토큰인 경우") {
                    val accessToken = "a.b.c"
                    every { tokenParser.parseSubject(accessToken) } throws Exception()

                    val results = authenticate(accessToken)
                    results.statusCode() shouldBe HttpStatus.UNAUTHORIZED.value()
                }
            }
        },
    )

private fun authenticate(accessToken: String) =
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

@RestController
class TestController {
    @GetMapping("/test")
    fun test(): AuthResponse<Any> = AuthResponse.success()
}
