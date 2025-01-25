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
class TokenGeneratorTest(
    private val jwtProperties: JwtProperties,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            val clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
            val now = LocalDateTime.now(clock)
            val tokenGenerator = TokenGenerator(jwtProperties, clock)

            context("발급") {
                val accessTokenParser = Jwts.parser().verifyWith(jwtProperties.accessSecretKey).build()
                val refreshTokenParser = Jwts.parser().verifyWith(jwtProperties.refreshSecretKey).build()

                test("성공") {
                    val account = AccountBuilder().build()

                    val actual = tokenGenerator.generate(account)

                    accessTokenParser.parseSignedClaims(actual.accessToken.value).payload.subject shouldBe "${account.id}"
                    refreshTokenParser.parseSignedClaims(actual.refreshToken.value).payload.subject shouldBe "${account.id}"
                }

                test("access token 만료 시간 검증") {
                    val account = AccountBuilder().build()

                    val actual = tokenGenerator.generate(account)

                    val expected = now + jwtProperties.accessTokenExpiresIn
                    actual.accessTokenExpiration shouldBe expected
                    accessTokenParser
                        .parseSignedClaims(actual.accessToken.value)
                        .payload.expiration
                        .toInstant()
                        .epochSecond shouldBe expected.toEpochSecond()
                }

                test("refresh token 만료 시간 검증") {
                    val account = AccountBuilder().build()

                    val actual = tokenGenerator.generate(account)

                    val expected = now + jwtProperties.refreshTokenExpiresIn
                    actual.refreshTokenExpiration shouldBe expected
                    refreshTokenParser
                        .parseSignedClaims(actual.refreshToken.value)
                        .payload.expiration
                        .toInstant()
                        .epochSecond shouldBe expected.toEpochSecond()
                }
            }
        },
    )
