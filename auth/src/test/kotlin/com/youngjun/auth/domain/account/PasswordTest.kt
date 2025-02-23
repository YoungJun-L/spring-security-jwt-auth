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
class PasswordTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf
            context("비밀번호 변경 여부 검증") {
                test("변경되지 않은 경우") {
                    val rawPassword = RawPasswordBuilder().build()
                    val password = Password.encodedWith(rawPassword, NoOperationPasswordEncoder)

                    shouldThrow<AuthException> { password.checkChanged(rawPassword, NoOperationPasswordEncoder) }
                        .errorType shouldBe ACCOUNT_UNCHANGED_PASSWORD
                }

                test("변경된 경우") {
                    val password = Password.encodedWith(RawPasswordBuilder().build(), NoOperationPasswordEncoder)

                    shouldNotThrow<AuthException> {
                        password.checkChanged(RawPasswordBuilder(value = "changedPassword").build(), NoOperationPasswordEncoder)
                    }
                }
            }

            context("비밀번호 인코딩") {
                test("성공") {
                    val rawPassword = RawPasswordBuilder().build()

                    val actual = Password.encodedWith(rawPassword, NoOperationPasswordEncoder)

                    NoOperationPasswordEncoder.matches(rawPassword.value, actual.value) shouldBe true
                }
            }
        },
    )
