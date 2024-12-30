package com.youngjun.auth.core.api.controller.v1

import com.youngjun.auth.api.controller.v1.request.ReissueTokenRequest
import com.youngjun.auth.core.api.support.AcceptanceTest
import com.youngjun.auth.core.api.support.context
import com.youngjun.auth.core.api.support.description
import com.youngjun.auth.core.api.support.document
import com.youngjun.auth.core.api.support.ignored
import com.youngjun.auth.core.api.support.type
import com.youngjun.auth.core.domain.token.RefreshToken
import com.youngjun.auth.core.domain.token.TokenParser
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
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

            context("재발급", listOf("/acceptance/auth.json", "/acceptance/token.json")) {
                test("성공") {
                    val refreshToken = "refreshToken"
                    every { tokenParser.verify(RefreshToken(refreshToken)) } just Runs

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
                        ).body(ReissueTokenRequest(refreshToken))
                        .post("/auth/token")
                        .then()
                        .log()
                        .all()
                }
            }

            context("유효하지 않은 refresh token 으로 재발급") {
                arrayOf("", " ").forEach { invalidRefreshToken ->
                    test("\"$invalidRefreshToken\"") {
                        given()
                            .log()
                            .all()
                            .contentType(ContentType.JSON)
                            .body(ReissueTokenRequest(invalidRefreshToken))
                            .post("/auth/token")
                            .then()
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                            .log()
                            .all()
                    }
                }
            }
        },
    )
