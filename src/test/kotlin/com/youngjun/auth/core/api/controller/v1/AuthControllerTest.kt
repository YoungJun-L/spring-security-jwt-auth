package com.youngjun.auth.core.api.controller.v1

import com.youngjun.auth.api.controller.v1.request.RegisterAuthRequest
import com.youngjun.auth.core.api.support.AcceptanceTest
import com.youngjun.auth.core.api.support.description
import com.youngjun.auth.core.api.support.document
import com.youngjun.auth.core.api.support.ignored
import com.youngjun.auth.core.api.support.type
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
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
class AuthControllerTest :
    FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("회원 가입") {
                test("성공") {
                    val validUsername = "username123"
                    val validPassword = "password123!"

                    given()
                        .log()
                        .all()
                        .contentType(ContentType.JSON)
                        .document(
                            "register",
                            requestFields(
                                "username" type STRING description "username, 최소 8자 이상 최대 50자 미만의 1개 이상 영문자, 1개 이상 숫자 조합",
                                "password" type STRING description "password, 최소 10자 이상 최대 50자 미만의 1개 이상 영문자, 1개 이상 특수문자, 1개 이상 숫자 조합",
                            ),
                            responseFields(
                                "status" type STRING description "status",
                                "data" type OBJECT description "data",
                                "data.userId" type NUMBER description "userId",
                                "error" type NULL ignored true,
                            ),
                        ).body(
                            RegisterAuthRequest(validUsername, validPassword),
                        ).post("/auth/register")
                        .then()
                        .log()
                        .all()
                }
            }

            context("유효하지 않은 아이디로 회원가입") {
                arrayOf(
                    "",
                    " ",
                    "abcd123",
                    "0123456789abcdefghijabcdefghijabcdefghijabcdefghij",
                    "abcdefgh",
                    "01234567",
                    "abcdef 123",
                ).forEach { invalidUsername ->
                    test("\"$invalidUsername\"") {
                        val validPassword = "password123!"

                        given()
                            .log()
                            .all()
                            .contentType(ContentType.JSON)
                            .body(RegisterAuthRequest(invalidUsername, validPassword))
                            .post("/auth/register")
                            .then()
                            .log()
                            .all()
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                    }
                }
            }

            context("유효하지 않은 비밀번호로 회원가입") {
                arrayOf(
                    "",
                    " ",
                    "abcdef123",
                    "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghij",
                    "abcdefgh",
                    "01234567",
                    "!@#$%^&*",
                    "abcdef 123 !",
                ).forEach { invalidPassword ->
                    val validUsername = "username123"

                    test("\"$invalidPassword\"") {
                        given()
                            .log()
                            .all()
                            .contentType(ContentType.JSON)
                            .body(RegisterAuthRequest(validUsername, invalidPassword))
                            .post("/auth/register")
                            .then()
                            .log()
                            .all()
                            .statusCode(HttpStatus.BAD_REQUEST.value())
                    }
                }
            }
        },
    )
