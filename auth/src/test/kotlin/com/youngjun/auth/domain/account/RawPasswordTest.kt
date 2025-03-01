package com.youngjun.auth.domain.account

import com.youngjun.auth.support.DomainTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_UNCHANGED_PASSWORD
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@DomainTest
class RawPasswordTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            context("비밀번호 길이 검증") {
                arrayOf("a".repeat(7), "a".repeat(65)).forEach { invalidPassword ->
                    test("\"$invalidPassword\"") {
                        shouldThrow<IllegalArgumentException> { RawPassword(invalidPassword) }
                    }
                }
            }

            context("비밀번호 변경 여부 검증") {
                test("변경되지 않은 경우") {
                    val rawPassword = RawPasswordBuilder().build()

                    shouldThrow<AuthException> { rawPassword.checkChanged(rawPassword) }
                        .errorType shouldBe ACCOUNT_UNCHANGED_PASSWORD
                }

                test("변경된 경우") {
                    val rawPassword = RawPasswordBuilder().build()

                    shouldNotThrow<AuthException> {
                        rawPassword.checkChanged(RawPasswordBuilder(value = "changedPassword").build())
                    }
                }
            }
        },
    )
