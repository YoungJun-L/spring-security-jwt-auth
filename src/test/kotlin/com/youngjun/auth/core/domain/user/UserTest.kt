package com.youngjun.auth.core.domain.user

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.USER_DISABLED_ERROR
import com.youngjun.auth.core.api.support.error.ErrorType.USER_LOCKED_ERROR
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class UserTest :
    FunSpec(
        {
            context("회원 상태 검증") {
                test("이용 가능") {
                    val user = UserBuilder(status = UserStatus.ENABLED).build()
                    shouldNotThrow<AuthException> { user.verify() }
                }

                test("계정 잠김") {
                    val user = UserBuilder(status = UserStatus.LOCKED).build()
                    shouldThrow<AuthException> { user.verify() }
                        .errorType shouldBe USER_LOCKED_ERROR
                }

                test("이용 제한") {
                    val user = UserBuilder(status = UserStatus.DISABLED).build()
                    shouldThrow<AuthException> { user.verify() }
                        .errorType shouldBe USER_DISABLED_ERROR
                }
            }
        },
    )
