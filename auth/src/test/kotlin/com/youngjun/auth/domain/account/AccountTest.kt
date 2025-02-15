package com.youngjun.auth.domain.account

import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DISABLED_ERROR
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_LOCKED_ERROR
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_LOGOUT_ERROR
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class AccountTest :
    FunSpec(
        {
            context("유저 상태 검증") {
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

                test("로그아웃") {
                    val account = AccountBuilder(status = AccountStatus.LOGOUT).build()

                    shouldThrow<AuthException> { account.verify() }
                        .errorType shouldBe ACCOUNT_LOGOUT_ERROR
                }
            }

            context("계정 활성화") {
                test("성공") {
                    val account = AccountBuilder(status = AccountStatus.DISABLED).build()

                    account.enable()

                    account.status shouldBe AccountStatus.ENABLED
                }
            }

            context("로그아웃") {
                test("성공") {
                    val account = AccountBuilder(status = AccountStatus.ENABLED).build()

                    account.logout()

                    account.status shouldBe AccountStatus.LOGOUT
                }
            }

            context("비밀번호 변경") {
                test("성공") {
                    val account = AccountBuilder().build()
                    val passwordEncoder = BCryptPasswordEncoder()
                    val newPassword = RawPasswordBuilder(value = "newPassword").build()

                    account.changePassword(newPassword.encodeWith(passwordEncoder))

                    passwordEncoder.matches(newPassword.value, account.password) shouldBe true
                }
            }
        },
    )
