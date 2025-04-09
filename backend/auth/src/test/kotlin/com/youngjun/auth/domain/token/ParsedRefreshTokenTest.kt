package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.support.hours
import com.youngjun.auth.domain.support.seconds
import com.youngjun.auth.support.DomainTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

@DomainTest
class ParsedRefreshTokenTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            context("만료 임박 여부 검증") {
                test("만료 임박한 경우") {
                    val now = LocalDateTime.now()
                    val threshold = 1.hours
                    val parsedRefreshToken = ParsedRefreshTokenBuilder(issuedAt = now, expiresIn = threshold).build()

                    parsedRefreshToken.isExpiringSoon(now, threshold) shouldBe true
                }

                test("임박하지 않은 경우") {
                    val now = LocalDateTime.now()
                    val threshold = 1.hours
                    val parsedRefreshToken = ParsedRefreshTokenBuilder(issuedAt = now, expiresIn = threshold + 1.seconds).build()

                    parsedRefreshToken.isExpiringSoon(now, threshold) shouldBe false
                }

                test("만료된 경우") {
                    val now = LocalDateTime.now()
                    val parsedRefreshToken = ParsedRefreshTokenBuilder(issuedAt = now, expiresIn = 0.seconds).build()

                    parsedRefreshToken.isExpiringSoon(now, 0.seconds) shouldBe false
                }
            }
        },
    )
