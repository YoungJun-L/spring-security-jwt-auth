package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.config.fixedClock
import com.youngjun.auth.domain.config.now
import com.youngjun.auth.domain.support.seconds
import com.youngjun.auth.domain.support.toEpochSecond
import com.youngjun.auth.security.config.JwtPropertiesBuilder
import com.youngjun.auth.support.DomainTest
import io.jsonwebtoken.Jwts
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@DomainTest
class JwtGeneratorTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val jwtProperties = JwtPropertiesBuilder().build()
            val jwtGenerator = JwtGenerator(jwtProperties, fixedClock)

            context("accessToken 발급") {
                val accessTokenParser = Jwts.parser().verifyWith(jwtProperties.accessSecretKey).build()

                test("성공") {
                    val userId = 1L

                    val actual = jwtGenerator.generateAccessToken(userId)

                    accessTokenParser.parseSignedClaims(actual.value).payload.subject shouldBe "$userId"
                }

                test("만료 시간 검증") {
                    val actual = jwtGenerator.generateAccessToken(1L)

                    val expected = now + jwtProperties.accessTokenExpiresIn
                    actual.expiration shouldBe expected
                    accessTokenParser
                        .parseSignedClaims(actual.value)
                        .payload.expiration
                        .toInstant()
                        .epochSecond shouldBe expected.toEpochSecond()
                }
            }

            context("refreshToken 발급") {
                val refreshTokenParser = Jwts.parser().verifyWith(jwtProperties.refreshSecretKey).build()

                test("성공") {
                    val userId = 1L

                    val actual = jwtGenerator.generateRefreshToken(userId)

                    refreshTokenParser.parseSignedClaims(actual.value).payload.subject shouldBe "$userId"
                }

                test("만료 시간 검증") {
                    val actual = jwtGenerator.generateRefreshToken(1L)

                    val expected = now + jwtProperties.refreshTokenExpiresIn
                    actual.expiration shouldBe expected
                    refreshTokenParser
                        .parseSignedClaims(actual.value)
                        .payload.expiration
                        .toInstant()
                        .epochSecond shouldBe expected.toEpochSecond()
                }
            }

            context("만료 시간에 따른 refreshToken 발급") {
                val refreshTokenParser = Jwts.parser().verifyWith(jwtProperties.refreshSecretKey).build()

                test("갱신되는 경우") {
                    val userId = 1L

                    val actual = jwtGenerator.generateRefreshTokenOnExpiration(userId, now + jwtProperties.expirationThreshold)

                    actual.isNotEmpty() shouldBe true
                    refreshTokenParser.parseSignedClaims(actual.value).payload.subject shouldBe "$userId"
                }

                test("만료 시간 검증") {
                    val actual = jwtGenerator.generateRefreshTokenOnExpiration(1L, now + jwtProperties.expirationThreshold)

                    val expected = now + jwtProperties.refreshTokenExpiresIn
                    actual.expiration shouldBe expected
                    refreshTokenParser
                        .parseSignedClaims(actual.value)
                        .payload.expiration
                        .toInstant()
                        .epochSecond shouldBe expected.toEpochSecond()
                }

                test("갱신되지 않는 경우") {
                    val actual = jwtGenerator.generateRefreshTokenOnExpiration(1L, now + jwtProperties.expirationThreshold + 1.seconds)

                    actual.isNotEmpty() shouldBe false
                }
            }
        },
    )
