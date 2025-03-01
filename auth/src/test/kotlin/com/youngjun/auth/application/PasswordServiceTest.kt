package com.youngjun.auth.application

import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.domain.account.Password
import com.youngjun.auth.domain.account.PasswordBuilder
import com.youngjun.auth.domain.account.RawPassword
import com.youngjun.auth.domain.account.RawPasswordBuilder
import com.youngjun.auth.support.ApplicationContextTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_BAD_CREDENTIALS
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_UNCHANGED_PASSWORD
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.security.crypto.password.PasswordEncoder

@ApplicationContextTest
class PasswordServiceTest(
    private val passwordService: PasswordService,
    private val passwordEncoder: PasswordEncoder,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("비밀번호 변경") {
                test("성공") {
                    val oldPassword = RawPassword("oldPassword")
                    val account = AccountBuilder(password = Password.encodedWith(oldPassword, passwordEncoder)).build()
                    val newPassword = RawPassword("newPassword")

                    val actual = passwordService.changePassword(account, oldPassword, newPassword)

                    actual.userId shouldBe account.id
                    actual.accessToken.userId shouldBe account.id
                    actual.refreshToken.userId shouldBe account.id
                    passwordEncoder.matches(newPassword.value, account.password) shouldBe true
                }

                test("현재 비밀번호와 일치하지 않으면 실패한다.") {
                    val wrongPassword = RawPassword("wrongPassword")
                    val account = AccountBuilder(password = PasswordBuilder().build()).build()

                    shouldThrow<AuthException> { passwordService.changePassword(account, wrongPassword, RawPasswordBuilder().build()) }
                        .errorType shouldBe ACCOUNT_BAD_CREDENTIALS
                }

                test("변경하려는 비밀번호가 현재 비밀번호와 동일하면 실패한다.") {
                    val oldPassword = RawPasswordBuilder().build()
                    val account = AccountBuilder(password = Password.encodedWith(oldPassword, passwordEncoder)).build()

                    shouldThrow<AuthException> { passwordService.changePassword(account, oldPassword, oldPassword) }
                        .errorType shouldBe ACCOUNT_UNCHANGED_PASSWORD
                }
            }
        },
    )
