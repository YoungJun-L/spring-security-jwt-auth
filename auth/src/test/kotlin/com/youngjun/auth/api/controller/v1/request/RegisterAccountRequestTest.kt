package com.youngjun.auth.api.controller.v1.request

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class RegisterAccountRequestTest :
    FunSpec(
        {
            context("검증") {
                test("성공") {
                    shouldNotThrow<IllegalArgumentException> { RegisterAccountRequest("username123", "password123!") }
                }
            }

            context("아이디 길이 검증") {
                arrayOf("a".repeat(7), "a".repeat(50)).forEach { invalidUsername ->
                    test("\"$invalidUsername\"") {
                        val validPassword = "password123!"

                        shouldThrow<IllegalArgumentException> { RegisterAccountRequest(invalidUsername, validPassword) }
                    }
                }
            }

            context("비밀번호 길이 검증") {
                arrayOf("a".repeat(7), "a".repeat(65)).forEach { invalidPassword ->
                    test("\"$invalidPassword\"") {
                        val validUsername = "username123"

                        shouldThrow<IllegalArgumentException> { RegisterAccountRequest(validUsername, invalidPassword) }
                    }
                }
            }
        },
    )
