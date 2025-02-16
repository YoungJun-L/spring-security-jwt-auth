package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EmailAddressBuilder
import com.youngjun.auth.support.DomainTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeInRange
import io.kotest.matchers.shouldBe

@DomainTest
class VerificationCodeTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            context("인증 코드 랜덤 생성") {
                test("성공") {
                    val emailAddress = EmailAddressBuilder().build()

                    val actual = VerificationCode.generate(emailAddress)

                    actual.emailAddress shouldBe emailAddress
                    actual.code.toInt() shouldBeInRange (0..<1_000_000)
                    actual.code.length shouldBe 6
                }
            }
        },
    )
