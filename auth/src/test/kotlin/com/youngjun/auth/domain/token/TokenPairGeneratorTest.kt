package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.support.seconds
import com.youngjun.auth.infra.jwt.JwtProperties
import com.youngjun.auth.support.DomainContextTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

@DomainContextTest
class TokenPairGeneratorTest(
    private val tokenPairGenerator: TokenPairGenerator,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProperties: JwtProperties,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("발급") {
                test("성공") {
                    val userId = 1L

                    val actual = tokenPairGenerator.generate(userId)

                    actual.userId shouldBe userId
                    actual.accessToken.userId shouldBe userId
                    actual.refreshToken.userId shouldBe userId
                }

                test("이전 refreshToken 은 교체된다.") {
                    val userId = 1L
                    refreshTokenRepository.save(RefreshTokenBuilder(userId).build())

                    val actual = tokenPairGenerator.generate(userId)

                    refreshTokenRepository.findByUserId(userId)!!.value shouldBe actual.refreshToken.value
                }
            }

            context("만료 시간에 따른 발급") {
                test("refreshToken 이 곧 만료되면 refreshToken 도 갱신된다.") {
                    val userId = 1L
                    val now = LocalDateTime.now()
                    val parsedRefreshToken =
                        ParsedRefreshTokenBuilder(
                            userId = userId,
                            issuedAt = now,
                            expiresIn = jwtProperties.expirationThreshold,
                        ).build()

                    val actual = tokenPairGenerator.generateOnExpiration(parsedRefreshToken, now)

                    actual.userId shouldBe userId
                    actual.accessToken.userId shouldBe userId
                    actual.refreshToken.userId shouldBe userId
                }

                test("refreshToken 이 아직 만료되지 않았으면 refreshToken 은 갱신되지 않는다.") {
                    val userId = 1L
                    val now = LocalDateTime.now()

                    val actual =
                        tokenPairGenerator.generateOnExpiration(
                            ParsedRefreshTokenBuilder(
                                userId = userId,
                                issuedAt = now,
                                expiresIn = jwtProperties.expirationThreshold + 1.seconds,
                            ).build(),
                            now,
                        )

                    actual.userId shouldBe userId
                    actual.accessToken.userId shouldBe userId
                    actual.refreshToken.isNotEmpty() shouldBe false
                }

                test("refreshToken 이 갱신되면 이전 refreshToken 과 교체된다.") {
                    val userId = 1L
                    val now = LocalDateTime.now()
                    val parsedRefreshToken =
                        ParsedRefreshTokenBuilder(
                            userId = userId,
                            issuedAt = now,
                            expiresIn = jwtProperties.expirationThreshold,
                        ).build()
                    refreshTokenRepository.save(RefreshTokenBuilder(userId = userId).build())

                    val actual = tokenPairGenerator.generateOnExpiration(parsedRefreshToken, now)

                    refreshTokenRepository.findByUserId(userId)!!.value shouldBe actual.refreshToken.value
                }

                test("refreshToken 이 갱신되지 않으면 이전 refreshToken 과 교체되지 않는다.") {
                    val userId = 1L
                    val now = LocalDateTime.now()
                    val parsedRefreshToken =
                        ParsedRefreshTokenBuilder(
                            userId = userId,
                            issuedAt = now,
                            expiresIn = jwtProperties.expirationThreshold + 1.seconds,
                        ).build()
                    val refreshToken = refreshTokenRepository.save(RefreshTokenBuilder(userId = userId).build())

                    tokenPairGenerator.generateOnExpiration(parsedRefreshToken, now)

                    refreshTokenRepository.findByUserId(userId)!!.value shouldBe refreshToken.value
                }
            }
        },
    )
