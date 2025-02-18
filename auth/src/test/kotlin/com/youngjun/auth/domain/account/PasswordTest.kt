package com.youngjun.auth.domain.account

import com.youngjun.auth.support.DomainTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@DomainTest
class PasswordTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            context("비밀번호 인코딩") {
                test("성공") {
                    val rawPassword = RawPasswordBuilder().build()

                    val actual = Password.encodedWith(rawPassword, NoOperationPasswordEncoder)

                    NoOperationPasswordEncoder.matches(rawPassword.value, actual.value) shouldBe true
                }
            }
        },
    )
