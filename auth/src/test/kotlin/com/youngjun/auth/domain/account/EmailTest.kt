package com.youngjun.auth.domain.account

import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec

class EmailTest :
    FunSpec(
        {
            context("이메일 검증 성공 케이스") {
                arrayOf(
                    "example@youngjun.com",
                    "Example@youngjun.com",
                    "example_12.3@youngjun.com",
                    "12345@youngjun.com",
                    "12.23.23@youngjun.com",
                    "e.x.a.m.p.l.e@youngjun.com",
                    "example_123@youngjun.com",
                    "example@young-jun.com",
                    "example@youngjun.co.kr",
                    "example@youngjun.dev.co.kr",
                    "example@youngjun123.com",
                ).forEach { validEmail ->
                    test("\"$validEmail\"") {
                        shouldNotThrow<IllegalArgumentException> { EmailAddress(validEmail) }
                    }
                }
            }

            context("이메일 검증 실패 케이스") {
                arrayOf(
                    "@youngjun.com",
                    "example@",
                    "example",
                    "example@youngjun",
                    "example@youngjun.",
                    "example@youngjun.c",
                    "exa mple@youngjun.com",
                    "example@young jun.com",
                    "example@youngjun.co kr",
                ).forEach { invalidEmail ->
                    test("\"$invalidEmail\"") {
                        shouldThrow<IllegalArgumentException> { EmailAddress(invalidEmail) }
                    }
                }
            }
        },
    )
