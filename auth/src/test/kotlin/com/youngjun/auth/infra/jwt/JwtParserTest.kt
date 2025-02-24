package com.youngjun.auth.infra.jwt

import com.youngjun.auth.domain.token.JwtBuilder
import com.youngjun.auth.domain.token.RawAccessToken
import com.youngjun.auth.domain.token.RawRefreshToken
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.TOKEN_EXPIRED
import com.youngjun.auth.support.error.ErrorType.TOKEN_INVALID
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Duration

class JwtParserTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val jwtProperties = JwtPropertiesBuilder().build()
            val jwtParser = JwtParser(jwtProperties)

            context("accessToken 파싱") {
                test("성공") {
                    val userId = 1L
                    val rawAccessToken =
                        RawAccessToken(JwtBuilder(secretKey = jwtProperties.accessSecretKey, subject = userId.toString()).build())

                    val actual = jwtParser.parse(rawAccessToken)

                    actual.userId shouldBe userId
                }

                test("accessToken 의 유효 기간이 지났으면 실패한다.") {
                    shouldThrow<AuthException> {
                        jwtParser.parse(
                            RawAccessToken(JwtBuilder(secretKey = jwtProperties.accessSecretKey, expiresIn = Duration.ZERO).build()),
                        )
                    }.errorType shouldBe TOKEN_EXPIRED
                }

                test("accessToken 이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { jwtParser.parse(RawAccessToken(JwtBuilder().build())) }
                        .errorType shouldBe TOKEN_INVALID
                }
            }

            context("refreshToken 파싱") {
                test("성공") {
                    val userId = 1L
                    val rawRefreshToken =
                        RawRefreshToken(JwtBuilder(secretKey = jwtProperties.refreshSecretKey, subject = userId.toString()).build())

                    val actual = jwtParser.parse(rawRefreshToken)

                    actual.userId shouldBe userId
                }

                test("refreshToken 의 유효 기간이 지났으면 실패한다.") {
                    shouldThrow<AuthException> {
                        jwtParser.parse(
                            RawRefreshToken(JwtBuilder(secretKey = jwtProperties.refreshSecretKey, expiresIn = Duration.ZERO).build()),
                        )
                    }.errorType shouldBe TOKEN_EXPIRED
                }

                test("refreshToken 이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { jwtParser.parse(RawRefreshToken(JwtBuilder().build())) }
                        .errorType shouldBe TOKEN_INVALID
                }
            }
        },
    )
