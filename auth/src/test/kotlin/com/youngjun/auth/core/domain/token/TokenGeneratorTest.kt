package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.domain.account.AccountBuilder
import com.youngjun.auth.core.domain.support.hours
import com.youngjun.auth.core.support.DomainTest
import io.jsonwebtoken.Jwts
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.Clock
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@DomainTest
class TokenGeneratorTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val secretKey =
                Jwts.SIG.HS256
                    .key()
                    .build()
            val accessExpiresIn = 1.hours
            val refreshExpiresIn = 12.hours
            val clock = Clock.fixed(Instant.now(), ZoneId.systemDefault())
            val now = LocalDateTime.now(clock)
            val tokenGenerator = TokenGenerator(SecretKeyHolder(secretKey), accessExpiresIn, refreshExpiresIn, clock)

            context("발급") {
                test("성공") {
                    val account = AccountBuilder().build()

                    val actual = tokenGenerator.generate(account)

                    val parser = Jwts.parser().verifyWith(secretKey).build()
                    parser.parseSignedClaims(actual.accessToken).payload.subject shouldBe "${account.id}"
                    parser.parseSignedClaims(actual.refreshToken).payload.subject shouldBe "${account.id}"
                }

                test("access token 만료 시간 검증") {
                    val account = AccountBuilder().build()

                    val actual = tokenGenerator.generate(account)

                    actual.accessTokenExpiration shouldBe now + accessExpiresIn
                }

                test("refresh token 만료 시간 검증") {
                    val account = AccountBuilder().build()

                    val actual = tokenGenerator.generate(account)

                    actual.refreshTokenExpiration shouldBe now + refreshExpiresIn
                }
            }
        },
    )
