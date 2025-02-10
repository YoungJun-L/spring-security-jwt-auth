package com.youngjun.auth.api.controller.v1.request

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class ChangePasswordRequestTest :
    FunSpec(
        {
            context("검증") {
                test("성공") {
                    shouldNotThrow<IllegalArgumentException> { ChangePasswordRequest("password123!") }
                }
            }

            context("비밀번호 길이 검증") {
                arrayOf("a".repeat(7), "a".repeat(65)).forEach { invalidPassword ->
                    test("\"$invalidPassword\"") { shouldThrow<IllegalArgumentException> { ChangePasswordRequest(invalidPassword) } }
                }
            }
        },
    )
