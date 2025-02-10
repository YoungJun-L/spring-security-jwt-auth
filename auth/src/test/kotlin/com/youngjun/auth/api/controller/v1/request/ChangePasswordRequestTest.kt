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

            context("비밀번호 검증") {
                arrayOf(
                    "",
                    " ",
                    "abcdef123",
                    "abcdefghijabcdefghijabcdefghijabcdefghijabcdefghij",
                    "abcdefgh",
                    "01234567",
                    "!@#$%^&*",
                    "abcdef 123 !",
                    "ㄱㄴㄷㄹㅁ123123!!@@",
                ).forEach { invalidPassword ->
                    test("\"$invalidPassword\"") { shouldThrow<IllegalArgumentException> { ChangePasswordRequest(invalidPassword) } }
                }
            }
        },
    )
