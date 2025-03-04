package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EMAIL_ADDRESS
import com.youngjun.auth.domain.support.minutes
import com.youngjun.auth.domain.support.seconds
import com.youngjun.auth.support.DomainTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.VERIFICATION_CODE_EXPIRED
import com.youngjun.auth.support.error.ErrorType.VERIFICATION_CODE_MISMATCHED
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@DomainTest
class VerificationCodeTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            context("인증 코드 랜덤 생성") {
                test("성공") {
                    val actual = generateVerificationCode(EMAIL_ADDRESS)

                    actual.emailAddress shouldBe EMAIL_ADDRESS
                    actual.code.value.all { it.isDigit() } shouldBe true
                    actual.code.value.length shouldBe 6
                }
            }

            context("일치, 유효 기간 검증") {
                test("일치하고 유효 기간이 지나지 않은 경우") {
                    val verificationCode = generateVerificationCode()

                    shouldNotThrow<AuthException> {
                        verificationCode.verifyWith(verificationCode.code, verificationCode.createdAt + 10.minutes - 1.seconds)
                    }
                }

                test("일치하나 유효 기간이 지난 경우") {
                    val verificationCode = generateVerificationCode()

                    shouldThrow<AuthException> {
                        verificationCode.verifyWith(verificationCode.code, verificationCode.createdAt + 10.minutes)
                    }.errorType shouldBe VERIFICATION_CODE_EXPIRED
                }

                test("일치하지 않는 경우") {
                    val verificationCode = generateVerificationCode()

                    shouldThrow<AuthException> { verificationCode.verifyWith(generateRawVerificationCodeExcluding(verificationCode)) }
                        .errorType shouldBe VERIFICATION_CODE_MISMATCHED
                }
            }
        },
    )
