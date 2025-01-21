package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.domain.account.AccountBuilder
import com.youngjun.auth.core.support.DomainTest
import io.jsonwebtoken.Jwts
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@DomainTest
class TokenGeneratorTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val secretKey =
                Jwts.SIG.HS256
                    .key()
                    .build()
            val secretKeyHolder = SecretKeyHolder(secretKey)
            val accessExpiresIn: Long = 2.hours.inWholeMilliseconds
            val refreshExpiresIn: Long = 1.days.inWholeMilliseconds
            val now = System.currentTimeMillis()
            val tokenGenerator = TokenGenerator(secretKeyHolder, { now }, accessExpiresIn, refreshExpiresIn)

            context("발급") {
                test("성공") {
                    val account = AccountBuilder().build()

                    val actual = tokenGenerator.generate(account)

                    val parser = Jwts.parser().verifyWith(secretKeyHolder.get()).build()
                    parser.parseSignedClaims(actual.accessToken).payload.subject shouldBe account.username
                    parser.parseSignedClaims(actual.refreshToken).payload.subject shouldBe account.username
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
