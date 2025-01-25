package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.api.security.JwtProperties
import com.youngjun.auth.core.support.DomainTest
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_INVALID_ERROR
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import java.time.Duration

@DomainTest
class TokenParserTest(
    private val tokenParser: TokenParser,
    private val jwtProperties: JwtProperties,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("accessToken userId 파싱") {
                test("성공") {
                    val userId = 1L
                    val accessToken =
                        AccessToken(
                            JwtBuilder(secretKey = jwtProperties.accessSecretKey, subject = userId.toString()).build(),
                        )

                    val actual = tokenParser.parseUserId(accessToken)

                    actual shouldBe userId
                }

                test("accessToken 의 만료 시간이 지났으면 실패한다.") {
                    shouldThrow<AuthException> {
                        tokenParser.parseUserId(
                            AccessToken(
                                JwtBuilder(secretKey = jwtProperties.accessSecretKey, expiresIn = Duration.ZERO).build(),
                            ),
                        )
                    }.errorType shouldBe TOKEN_EXPIRED_ERROR
                }

                test("accessToken 이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { tokenParser.parseUserId(AccessToken(JwtBuilder().build())) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }
            }

            context("refreshToken userId 파싱") {
                test("성공") {
                    val userId = 1L
                    val refreshToken =
                        RefreshToken(
                            JwtBuilder(secretKey = jwtProperties.refreshSecretKey, subject = userId.toString()).build(),
                        )

                    val actual = tokenParser.parseUserId(refreshToken)

                    actual shouldBe userId
                }

                test("refreshToken 의 만료 시간이 지났으면 실패한다.") {
                    shouldThrow<AuthException> {
                        tokenParser.parseUserId(
                            RefreshToken(
                                JwtBuilder(secretKey = jwtProperties.refreshSecretKey, expiresIn = Duration.ZERO).build(),
                            ),
                        )
                    }.errorType shouldBe TOKEN_EXPIRED_ERROR
                }

                test("refreshToken 이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { tokenParser.parseUserId(RefreshToken(JwtBuilder().build())) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }
            }
        },
    )
