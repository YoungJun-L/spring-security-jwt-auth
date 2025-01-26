package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.api.security.JwtProperties
import com.youngjun.auth.core.domain.account.AccountBuilder
import com.youngjun.auth.core.domain.support.toEpochSecond
import com.youngjun.auth.core.support.DomainTest
import io.jsonwebtoken.Jwts
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@DomainTest
class JwtGeneratorTest(
    private val jwtProperties: JwtProperties,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            val clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
            val now = LocalDateTime.now(clock)
            val jwtGenerator = JwtGenerator(jwtProperties, clock)

            context("accessToken 발급") {
                val accessTokenParser = Jwts.parser().verifyWith(jwtProperties.accessSecretKey).build()

                test("성공") {
                    val account = AccountBuilder().build()

                    val actual = jwtGenerator.generateAccessToken(account)

                    accessTokenParser.parseSignedClaims(actual.value).payload.subject shouldBe "${account.id}"
                }

                test("access token 만료 시간 검증") {
                    val account = AccountBuilder().build()

                    val actual = jwtGenerator.generateAccessToken(account)

                    val expected = now + jwtProperties.accessTokenExpiresIn
                    actual.expiration shouldBe expected
                    accessTokenParser
                        .parseSignedClaims(actual.value)
                        .payload.expiration
                        .toInstant()
                        .epochSecond shouldBe
                        expected.toEpochSecond()
                }
            }

            context("refreshToken 발급") {
                val refreshTokenParser = Jwts.parser().verifyWith(jwtProperties.refreshSecretKey).build()

                test("성공") {
                    val account = AccountBuilder().build()

                    val actual = jwtGenerator.generateRefreshToken(account)

                    refreshTokenParser.parseSignedClaims(actual.value).payload.subject shouldBe "${account.id}"
                }

                test("refresh token 만료 시간 검증") {
                    val account = AccountBuilder().build()

                    val actual = jwtGenerator.generateRefreshToken(account)

                    val expected = now + jwtProperties.refreshTokenExpiresIn
                    actual.expiration shouldBe expected
                    refreshTokenParser
                        .parseSignedClaims(actual.value)
                        .payload.expiration
                        .toInstant()
                        .epochSecond shouldBe expected.toEpochSecond()
                }
            }
        },
    )
