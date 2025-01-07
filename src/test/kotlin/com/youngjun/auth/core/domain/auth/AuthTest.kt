package com.youngjun.auth.core.domain.auth

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class AuthTest :
    FunSpec(
        {
            context("회원 상태 검증") {
                test("이용 가능") {
                    val auth = AuthBuilder(status = AuthStatus.ENABLED).build()
                    shouldNotThrow<AuthException> { auth.verify() }
                }

                test("계정 잠김") {
                    val auth = AuthBuilder(status = AuthStatus.LOCKED).build()
                    shouldThrow<AuthException> { auth.verify() }
                        .errorType shouldBe ErrorType.AUTH_LOCKED_ERROR
                }

                test("이용 제한") {
                    val auth = AuthBuilder(status = AuthStatus.DISABLED).build()
                    shouldThrow<AuthException> { auth.verify() }
                        .errorType shouldBe ErrorType.AUTH_DISABLED_ERROR
                }
            }

            context("유저 정보 조회") {
                test("성공") {
                    val auth = AuthBuilder().build()
                    auth.details()["username"] shouldBe auth.username
                }
            }
        },
    )
