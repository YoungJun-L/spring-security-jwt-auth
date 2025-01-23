package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.DomainTest
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_INVALID_ERROR
import io.jsonwebtoken.Jwts
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Duration

@DomainTest
class TokenParserTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val secretKey =
                Jwts.SIG.HS256
                    .key()
                    .build()
            val tokenParser = TokenParser(SecretKeyHolder(secretKey))

            context("refreshToken userId 파싱") {
                test("성공") {
                    val userId = 1L
                    val refreshToken =
                        RefreshToken(JwtBuilder(secretKey = secretKey, subject = userId.toString()).build())

                    val actual = tokenParser.parseUserId(refreshToken)

                    actual shouldBe userId
                }

                test("토큰의 만료 시간이 지났으면 실패한다.") {
                    val refreshToken =
                        RefreshToken(JwtBuilder(secretKey = secretKey, expiresIn = Duration.ZERO).build())

                    shouldThrow<AuthException> { tokenParser.parseUserId(refreshToken) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }

                test("토큰이 유효하지 않으면 실패한다.") {
                    val refreshToken = RefreshToken(JwtBuilder().build())

                    shouldThrow<AuthException> { tokenParser.parseUserId(refreshToken) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }
            }

            context("accessToken userId 파싱") {
                test("성공") {
                    val userId = 1L
                    val accessToken =
                        AccessToken(JwtBuilder(secretKey = secretKey, subject = userId.toString()).build())

                    val actual = tokenParser.parseUserId(accessToken)

                    actual shouldBe userId
                }

                test("토큰의 만료 시간이 지났으면 실패한다.") {
                    val accessToken =
                        AccessToken(JwtBuilder(secretKey = secretKey, expiresIn = Duration.ZERO).build())

                    shouldThrow<AuthException> { tokenParser.parseUserId(accessToken) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }

                test("토큰이 유효하지 않으면 실패한다.") {
                    val accessToken = AccessToken(JwtBuilder().build())

                    shouldThrow<AuthException> { tokenParser.parseUserId(accessToken) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }
            }
        },
    )
