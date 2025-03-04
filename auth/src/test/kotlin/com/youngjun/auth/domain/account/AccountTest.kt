package com.youngjun.auth.domain.account

import com.youngjun.auth.support.DomainTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_BAD_CREDENTIALS
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DISABLED
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_LOCKED
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_LOGOUT
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@DomainTest
class AccountTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            context("상태 검증") {
                test("이용 가능") {
                    val account = AccountBuilder(status = AccountStatus.ENABLED).build()

                    shouldNotThrow<AuthException> { account.verify() }
                }

                test("계정 잠김") {
                    val account = AccountBuilder(status = AccountStatus.LOCKED).build()

                    shouldThrow<AuthException> { account.verify() }
                        .errorType shouldBe ACCOUNT_LOCKED
                }

                test("이용 제한") {
                    val account = AccountBuilder(status = AccountStatus.DISABLED).build()

                    shouldThrow<AuthException> { account.verify() }
                        .errorType shouldBe ACCOUNT_DISABLED
                }

                test("로그아웃") {
                    val account = AccountBuilder(status = AccountStatus.LOGOUT).build()

                    shouldThrow<AuthException> { account.verify() }
                        .errorType shouldBe ACCOUNT_LOGOUT
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

            context("비밀번호 일치 여부 검증") {
                test("일치하지 않는 경우") {
                    val account = AccountBuilder(password = Password.encodedWith(RAW_PASSWORD, NoOperationPasswordEncoder)).build()

                    shouldThrow<AuthException> { account.verify(RawPassword("wrongPassword"), NoOperationPasswordEncoder) }
                        .errorType shouldBe ACCOUNT_BAD_CREDENTIALS
                }

                test("일치하는 경우") {
                    val account = AccountBuilder(password = Password.encodedWith(RAW_PASSWORD, NoOperationPasswordEncoder)).build()

                    shouldNotThrow<AuthException> { account.verify(RAW_PASSWORD, NoOperationPasswordEncoder) }
                }
            }

            context("비밀번호 변경") {
                test("성공") {
                    val account = AccountBuilder().build()
                    val newPassword = RawPassword(value = "newPassword")

                    account.changePassword(newPassword, NoOperationPasswordEncoder)

                    NoOperationPasswordEncoder.matches(newPassword.value, account.password) shouldBe true
                }
            }

            context("계정 이용 제한 여부 검증") {
                test("이용 가능한 경우") {
                    val account = AccountBuilder().build()

                    shouldNotThrow<AuthException> { account.checkNotDisabled() }
                }

                test("이용 제한된 경우") {
                    val account = AccountBuilder(status = AccountStatus.DISABLED).build()

                    shouldThrow<AuthException> { account.checkNotDisabled() }
                        .errorType shouldBe ACCOUNT_DISABLED
                }
            }
        },
    )
