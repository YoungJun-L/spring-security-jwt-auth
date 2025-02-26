package com.youngjun.auth.infra.jwt

import com.youngjun.auth.domain.support.toEpochSecond
import io.jsonwebtoken.Jwts
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class JwtGeneratorTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val jwtProperties = JwtPropertiesBuilder().build()
            val jwtGenerator = JwtGenerator(jwtProperties)

            val accessTokenParser = Jwts.parser().verifyWith(jwtProperties.accessSecretKey).build()
            val refreshTokenParser = Jwts.parser().verifyWith(jwtProperties.refreshSecretKey).build()

            context("accessToken 발급") {
                test("성공") {
                    val userId = 1L

                    val actual = jwtGenerator.generateAccessToken(userId)

                    actual.userId shouldBe userId
                    accessTokenParser.parseSignedClaims(actual.value.rawValue).payload.subject shouldBe "$userId"
                }

                test("만료 시간 검증") {
                    val now = LocalDateTime.now()

                    val actual = jwtGenerator.generateAccessToken(1L, now)

                    val expected = now + jwtProperties.accessTokenExpiresIn
                    actual.expiration shouldBe expected
                    accessTokenParser
                        .parseSignedClaims(actual.value.rawValue)
                        .payload.expiration
                        .toInstant()
                        .epochSecond shouldBe expected.toEpochSecond()
                }
            }

            context("refreshToken 발급") {
                test("성공") {
                    val userId = 1L

                    val actual = jwtGenerator.generateRefreshToken(userId)

                    actual.userId shouldBe userId
                    refreshTokenParser.parseSignedClaims(actual.value.rawValue).payload.subject shouldBe "$userId"
                }

                test("만료 시간 검증") {
                    val now = LocalDateTime.now()

                    val actual = jwtGenerator.generateRefreshToken(1L, now)

                    val expected = now + jwtProperties.refreshTokenExpiresIn
                    actual.expiration shouldBe expected
                    refreshTokenParser
                        .parseSignedClaims(actual.value.rawValue)
                        .payload.expiration
                        .toInstant()
                        .epochSecond shouldBe expected.toEpochSecond()
                }
            }
        },
    )
