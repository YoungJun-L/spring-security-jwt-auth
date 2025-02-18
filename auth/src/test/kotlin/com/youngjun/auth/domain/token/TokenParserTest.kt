package com.youngjun.auth.domain.token

import com.youngjun.auth.infra.db.RefreshTokenJpaRepository
import com.youngjun.auth.security.config.JwtProperties
import com.youngjun.auth.support.DomainContextTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.TOKEN_EXPIRED
import com.youngjun.auth.support.error.ErrorType.TOKEN_INVALID
import com.youngjun.auth.support.error.ErrorType.TOKEN_NOT_FOUND
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import java.time.Duration

@DomainContextTest
class TokenParserTest(
    private val tokenParser: TokenParser,
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
    private val jwtProperties: JwtProperties,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("accessToken 파싱") {
                test("성공") {
                    val userId = 1L
                    val rawAccessToken =
                        RawAccessToken(JwtBuilder(secretKey = jwtProperties.accessSecretKey, subject = userId.toString()).build())

                    val actual = tokenParser.parse(rawAccessToken)

                    actual.userId shouldBe userId
                }

                test("accessToken 의 유효 기간이 지났으면 실패한다.") {
                    shouldThrow<AuthException> {
                        tokenParser.parse(
                            RawAccessToken(JwtBuilder(secretKey = jwtProperties.accessSecretKey, expiresIn = Duration.ZERO).build()),
                        )
                    }.errorType shouldBe TOKEN_EXPIRED
                }

                test("accessToken 이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { tokenParser.parse(RawAccessToken(JwtBuilder().build())) }
                        .errorType shouldBe TOKEN_INVALID
                }
            }

            context("refreshToken 파싱") {
                test("성공") {
                    val userId = 1L
                    val rawRefreshToken =
                        RawRefreshToken(JwtBuilder(secretKey = jwtProperties.refreshSecretKey, subject = userId.toString()).build())
                    refreshTokenJpaRepository.save(RefreshTokenBuilder(userId, rawRefreshToken.value).build())

                    val actual = tokenParser.parse(rawRefreshToken)

                    actual.userId shouldBe userId
                }

                test("refreshToken 의 유효 기간이 지났으면 실패한다.") {
                    shouldThrow<AuthException> {
                        tokenParser.parse(
                            RawRefreshToken(JwtBuilder(secretKey = jwtProperties.refreshSecretKey, expiresIn = Duration.ZERO).build()),
                        )
                    }.errorType shouldBe TOKEN_EXPIRED
                }

                test("refreshToken 이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { tokenParser.parse(RawRefreshToken(JwtBuilder().build())) }
                        .errorType shouldBe TOKEN_INVALID
                }

                test("refreshToken 이 만료되었으면 실패한다.") {
                    val userId = 1L
                    val rawRefreshToken =
                        RawRefreshToken(JwtBuilder(secretKey = jwtProperties.refreshSecretKey, subject = userId.toString()).build())
                    refreshTokenJpaRepository.save(RefreshTokenBuilder(userId, rawRefreshToken.value, TokenStatus.EXPIRED).build())

                    shouldThrow<AuthException> { tokenParser.parse(rawRefreshToken) }
                        .errorType shouldBe TOKEN_EXPIRED
                }

                test("refreshToken 이 존재하지 않으면 실패한다.") {
                    shouldThrow<AuthException> {
                        tokenParser.parse(RawRefreshToken(JwtBuilder(secretKey = jwtProperties.refreshSecretKey).build()))
                    }.errorType shouldBe TOKEN_NOT_FOUND
                }
            }
        },
    )
