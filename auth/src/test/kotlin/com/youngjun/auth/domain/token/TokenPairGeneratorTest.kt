package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.support.hours
import com.youngjun.auth.domain.support.seconds
import com.youngjun.auth.security.config.JwtPropertiesBuilder
import com.youngjun.auth.support.DomainTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

@DomainTest
class TokenPairGeneratorTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val jwtProperties = JwtPropertiesBuilder().build()
            val tokenPairGenerator = TokenPairGenerator(JwtGenerator(jwtProperties))

            context("발급") {
                test("성공") {
                    val userId = 1L

                    val actual = tokenPairGenerator.generate(userId)

                    actual.userId shouldBe userId
                }
            }

            context("만료 시간에 따른 발급") {
                test("성공") {
                    val userId = 1L

                    val actual = tokenPairGenerator.generateOnExpiration(ParsedRefreshTokenBuilder(userId).build())

                    actual.userId shouldBe userId
                }

                test("refreshToken 이 곧 만료되면 refreshToken 도 갱신된다.") {
                    val now = LocalDateTime.now()
                    val parsedRefreshToken =
                        ParsedRefreshTokenBuilder(
                            userId = 1L,
                            issuedAt = now,
                            expiresIn = jwtProperties.expirationThreshold - 1.seconds,
                        ).build()

                    val actual = tokenPairGenerator.generateOnExpiration(parsedRefreshToken, now)

                    actual.refreshToken.isNotEmpty() shouldBe true
                }

                test("refreshToken 이 아직 만료되지 않았으면 refreshToken 은 갱신되지 않는다.") {
                    val now = LocalDateTime.now()

                    val actual =
                        tokenPairGenerator.generateOnExpiration(
                            ParsedRefreshTokenBuilder(
                                userId = 1L,
                                issuedAt = now,
                                expiresIn = jwtProperties.expirationThreshold + 1.hours,
                            ).build(),
                            now,
                        )

                    actual.refreshToken.isNotEmpty() shouldBe false
                }
            }
        },
    )
