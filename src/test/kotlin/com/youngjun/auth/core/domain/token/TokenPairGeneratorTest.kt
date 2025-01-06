package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.domain.auth.AuthBuilder
import com.youngjun.auth.core.domain.support.DomainTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.extensions.time.withConstantNow
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime.now
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@DomainTest
class TokenPairGeneratorTest(
    private val tokenPairGenerator: TokenPairGenerator,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("발급") {
                test("access token 은 30분간 유효하다.") {
                    val now = now()
                    withConstantNow(now) {
                        val auth = AuthBuilder().build()

                        val actual = tokenPairGenerator.issue(auth)

                        actual.accessTokenExpiration shouldBe now.toEpochSecond(ZoneOffset.UTC) + 30.minutes.inWholeSeconds
                    }
                }

                test("refresh token 은 30일간 유효하다.") {
                    val now = now()
                    withConstantNow(now) {
                        val auth = AuthBuilder().build()

                        val actual = tokenPairGenerator.issue(auth)

                        actual.refreshTokenExpiration shouldBe now.toEpochSecond(ZoneOffset.UTC) + 30.days.inWholeSeconds
                    }
                }
            }
        },
    )
