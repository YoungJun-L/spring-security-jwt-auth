package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.api.controller.v1.request.LoginRequest
import com.youngjun.auth.core.api.support.AcceptanceTest
import com.youngjun.auth.core.api.support.VALID_PASSWORD
import com.youngjun.auth.core.api.support.VALID_USERNAME
import com.youngjun.auth.core.api.support.context
import com.youngjun.auth.core.api.support.description
import com.youngjun.auth.core.api.support.document
import com.youngjun.auth.core.api.support.ignored
import com.youngjun.auth.core.api.support.type
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
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
class LoginTest :
    FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("로그인", listOf("/acceptance/auth.json")) {
                test("성공") {
                    given()
                        .log()
                        .all()
                        .contentType(ContentType.JSON)
                        .body(LoginRequest(VALID_USERNAME, VALID_PASSWORD))
                        .document(
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
                        ).post("/auth/login")
                        .then()
                        .log()
                        .all()
                }

                test("미가입 유저인 경우") {
                    val username = "username000"

                    val results = login(username)
                    results.statusCode() shouldBe HttpStatus.UNAUTHORIZED.value()
                }

                test("이용 제한 유저인 경우") {
                    val username = "username456"

                    val results = login(username)
                    results.statusCode() shouldBe HttpStatus.FORBIDDEN.value()
                }
            }
        },
    )

private fun login(
    username: String = VALID_USERNAME,
    password: String = VALID_PASSWORD,
) = given()
    .log()
    .all()
    .contentType(ContentType.JSON)
    .body(LoginRequest(username, password))
    .post("/auth/login")
    .then()
    .log()
    .all()
    .extract()
