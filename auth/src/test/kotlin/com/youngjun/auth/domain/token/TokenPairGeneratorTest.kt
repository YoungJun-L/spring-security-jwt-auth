package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.support.hours
import com.youngjun.auth.domain.support.seconds
import com.youngjun.auth.infra.db.RefreshTokenJpaRepository
import com.youngjun.auth.security.config.JwtProperties
import com.youngjun.auth.support.DomainTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDateTime

@DomainTest
class TokenPairGeneratorTest(
    private val tokenPairGenerator: TokenPairGenerator,
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
    private val jwtProperties: JwtProperties,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("발급") {
                test("성공") {
                    val userId = 1L

                    val actual = tokenPairGenerator.issue(userId)

                    actual.userId shouldBe userId
                }

                test("이전 refreshToken 은 교체된다.") {
                    val userId = 1L

                    val expected = tokenPairGenerator.issue(userId)

                    refreshTokenJpaRepository.findByUserId(userId)!!.value shouldBe expected.refreshToken.value
                }
            }

            context("재발급") {
                test("성공") {
                    val userId = 1L

                    val actual = tokenPairGenerator.reissue(ParsedRefreshTokenBuilder(userId).build())

                    actual.userId shouldBe userId
                }

                test("refreshToken 이 갱신되면 저장된 토큰의 값이 변경된다.") {
                    val userId = 1L
                    val parsedRefreshToken =
                        ParsedRefreshTokenBuilder(
                            userId = userId,
                            issuedAt = LocalDateTime.now(),
                            expiresIn = jwtProperties.expirationThreshold - 1.seconds,
                        ).build()
                    val refreshToken = refreshTokenJpaRepository.save(RefreshTokenBuilder(userId, parsedRefreshToken.value).build())

                    val expected = tokenPairGenerator.reissue(parsedRefreshToken)

                    refreshTokenJpaRepository.findByIdOrNull(refreshToken.id)!!.value shouldBe expected.refreshToken.value
                }

                test("refreshToken 이 갱신되지 않으면 저장된 토큰의 값이 변경되지 않는다.") {
                    val userId = 1L
                    val parsedRefreshToken =
                        ParsedRefreshTokenBuilder(
                            userId = userId,
                            issuedAt = LocalDateTime.now(),
                            expiresIn = jwtProperties.expirationThreshold + 1.hours,
                        ).build()
                    val refreshToken = refreshTokenJpaRepository.save(RefreshTokenBuilder(userId, parsedRefreshToken.value).build())

                    tokenPairGenerator.reissue(parsedRefreshToken)

                    refreshTokenJpaRepository.findByIdOrNull(refreshToken.id)!!.value shouldBe parsedRefreshToken.value
                }

                test("refreshToken 이 곧 만료되면 refresh token 도 갱신된다.") {
                    val userId = 1L
                    val parsedRefreshToken =
                        ParsedRefreshTokenBuilder(
                            userId = userId,
                            issuedAt = LocalDateTime.now(),
                            expiresIn = jwtProperties.expirationThreshold - 1.seconds,
                        ).build()
                    refreshTokenJpaRepository.save(RefreshTokenBuilder(userId, parsedRefreshToken.value).build())

                    val actual = tokenPairGenerator.reissue(parsedRefreshToken)

                    actual.refreshToken.exists() shouldBe true
                }

                test("refreshToken 이 아직 만료되지 않았으면 refresh token 은 갱신되지 않는다.") {
                    val userId = 1L

                    val actual =
                        tokenPairGenerator.reissue(
                            ParsedRefreshTokenBuilder(
                                userId = userId,
                                issuedAt = LocalDateTime.now(),
                                expiresIn = jwtProperties.expirationThreshold + 1.hours,
                            ).build(),
                        )

                    actual.refreshToken.exists() shouldBe false
                }
            }
        },
    )
