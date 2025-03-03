package com.youngjun.auth.domain.account

import com.youngjun.auth.support.DomainTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_BAD_CREDENTIALS
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
            context("비밀번호 일치 여부 검증") {
                test("일치하지 않는 경우") {
                    val password = Password.encodedWith(RAW_PASSWORD, NoOperationPasswordEncoder)

                    shouldThrow<AuthException> { password.verify(RawPassword("wrongPassword"), NoOperationPasswordEncoder) }
                        .errorType shouldBe ACCOUNT_BAD_CREDENTIALS
                }

                test("일치하는 경우") {
                    val password = Password.encodedWith(RAW_PASSWORD, NoOperationPasswordEncoder)

                    shouldNotThrow<AuthException> { password.verify(RAW_PASSWORD, NoOperationPasswordEncoder) }
                }
            }

            context("비밀번호 인코딩") {
                test("성공") {
                    val actual = Password.encodedWith(RAW_PASSWORD, NoOperationPasswordEncoder)

                    NoOperationPasswordEncoder.matches(RAW_PASSWORD.value, actual.value) shouldBe true
                }
            }
        },
    )
