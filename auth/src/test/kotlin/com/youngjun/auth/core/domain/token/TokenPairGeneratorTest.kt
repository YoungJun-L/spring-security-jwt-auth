package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.domain.support.DomainTest
import com.youngjun.auth.core.domain.user.UserBuilder
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.time.withConstantNow
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime.now
import java.time.ZoneId
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@DomainTest
class TokenPairGeneratorTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val secretKey = "012345678abcdefghijklmnopqrstuvwxyz"
            val accessExpiresIn = 30.minutes.inWholeSeconds
            val refreshExpiresIn = 30.days.inWholeSeconds
            val tokenPairGenerator = TokenPairGenerator(secretKey, accessExpiresIn, refreshExpiresIn)

            context("발급") {
                test("성공") {
                    val user = UserBuilder().build()

                    val actual = tokenPairGenerator.issue(user)

                    val parser = Jwts.parser().verifyWith(Keys.hmacShaKeyFor(secretKey.toByteArray())).build()
                    parser.parseSignedClaims(actual.accessToken).payload.subject shouldBe user.username
                    parser.parseSignedClaims(actual.refreshToken).payload.subject shouldBe user.username
                }

                test("access token 만료 시간 검증") {
                    val now = now()
                    withConstantNow(now) {
                        val user = UserBuilder().build()

                        val actual = tokenPairGenerator.issue(user)

                        actual.accessTokenExpiration shouldBe
                            now.atZone(ZoneId.systemDefault()).toEpochSecond() + accessExpiresIn
                    }
                }

                test("refresh token 만료 시간 검증") {
                    val now = now()
                    withConstantNow(now) {
                        val user = UserBuilder().build()

                        val actual = tokenPairGenerator.issue(user)

                        actual.refreshTokenExpiration shouldBe
                            now.atZone(ZoneId.systemDefault()).toEpochSecond() + refreshExpiresIn
                    }
                }
            }
        },
    )
