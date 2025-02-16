package com.youngjun.auth.domain.account

import com.youngjun.auth.support.DomainTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

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

            context("비밀번호 인코딩") {
                test("성공") {
                    val rawPassword = RawPasswordBuilder().build()
                    val passwordEncoder = BCryptPasswordEncoder()

                    val actual = rawPassword.encodeWith(passwordEncoder)

                    passwordEncoder.matches(rawPassword.value, actual.value) shouldBe true
                }
            }
        },
    )
