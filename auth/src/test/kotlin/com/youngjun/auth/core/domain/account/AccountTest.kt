package com.youngjun.auth.core.domain.account

import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.ACCOUNT_DISABLED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.ACCOUNT_LOCKED_ERROR
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AccountTest :
    FunSpec(
        {
            context("회원 상태 검증") {
                test("이용 가능") {
                    val account = AccountBuilder(status = AccountStatus.ENABLED).build()
                    shouldNotThrow<AuthException> { account.verify() }
                }

                test("계정 잠김") {
                    val account = AccountBuilder(status = AccountStatus.LOCKED).build()
                    shouldThrow<AuthException> { account.verify() }
                        .errorType shouldBe ACCOUNT_LOCKED_ERROR
                }

                test("이용 제한") {
                    val account = AccountBuilder(status = AccountStatus.DISABLED).build()
                    shouldThrow<AuthException> { account.verify() }
                        .errorType shouldBe ACCOUNT_DISABLED_ERROR
                }
            }
        },
    )
