package com.youngjun.auth.core.api.controller.v1

import com.youngjun.auth.core.api.controller.v1.request.ReissueTokenRequest
import com.youngjun.auth.core.api.support.AcceptanceTest
import com.youngjun.auth.core.api.support.context
import com.youngjun.auth.core.api.support.description
import com.youngjun.auth.core.api.support.document
import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType
import com.youngjun.auth.core.api.support.ignored
import com.youngjun.auth.core.api.support.type
import com.youngjun.auth.core.domain.token.RefreshToken
import com.youngjun.auth.core.domain.token.TokenParser
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.springframework.http.HttpStatus
import org.springframework.restdocs.payload.JsonFieldType.NULL
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.OBJECT
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields

@AcceptanceTest
class TokenControllerTest(
    private val tokenParser: TokenParser,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("재발급", listOf("/acceptance/token.json", "/acceptance/auth.json")) {
                test("성공") {
                    val value = "refreshToken1"
                    every { tokenParser.verify(RefreshToken(value)) } just Runs

                    given()
                        .log()
                        .all()
                        .contentType(ContentType.JSON)
                        .document(
                            "token",
                            requestFields(
                                "refreshToken" type STRING description "refreshToken",
                            ),
                            responseFields(
                                "status" type STRING description "status",
                                "data" type OBJECT description "data",
                                "data.accessToken" type STRING description "accessToken",
                                "data.accessTokenExpiresIn" type NUMBER description "accessToken 만료 시간, UNIX 타임스탬프(Timestamp)",
                                "data.refreshToken" type STRING description "refreshToken",
                                "data.refreshTokenExpiresIn" type NUMBER description "refreshToken 만료 시간, UNIX 타임스탬프(Timestamp)",
                                "error" type NULL ignored true,
                            ),
                        ).body(ReissueTokenRequest(value))
                        .post("/auth/token")
                        .then()
                        .log()
                        .all()
                }

                test("이용 제한 유저인 경우") {
                    val value = "refreshToken2"
                    every { tokenParser.verify(RefreshToken(value)) } just Runs

                    val results = reissue(value)
                    results.statusCode() shouldBe HttpStatus.FORBIDDEN.value()
                }

                test("잘못된 값인 경우") {
                    val value = "INVALID"
                    every { tokenParser.verify(RefreshToken(value)) } throws AuthException(ErrorType.TOKEN_INVALID_ERROR)

                    val results = reissue(value)
                    results.statusCode() shouldBe HttpStatus.UNAUTHORIZED.value()
                }
            }
        },
    )

private fun reissue(refreshToken: String) =
    given()
        .log()
        .all()
        .contentType(ContentType.JSON)
        .body(ReissueTokenRequest(refreshToken))
        .post("/auth/token")
        .then()
        .log()
        .all()
        .extract()
