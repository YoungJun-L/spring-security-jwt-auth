package com.youngjun.auth.core.api.controller.v1.request

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class RegisterAccountRequestTest :
    FunSpec(
        {
            context("아이디 검증") {
                arrayOf(
                    "",
                    " ",
                    "abcd123",
                    "0123456789abcdefghijabcdefghijabcdefghijabcdefghij",
                    "abcdefgh",
                    "01234567",
                    "abcdef 123",
                ).forEach { invalidUsername ->
                    test("\"$invalidUsername\"") {
                        val validPassword = "password123!"

                        shouldThrow<IllegalArgumentException> {
                            RegisterAccountRequest(invalidUsername, validPassword).toNewAccount()
                        }
                    }
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
                ).forEach { invalidPassword ->
                    test("\"$invalidPassword\"") {
                        val validUsername = "username123"

                        shouldThrow<IllegalArgumentException> {
                            RegisterAccountRequest(validUsername, invalidPassword).toNewAccount()
                        }
                    }
                }
            }
        },
    )
