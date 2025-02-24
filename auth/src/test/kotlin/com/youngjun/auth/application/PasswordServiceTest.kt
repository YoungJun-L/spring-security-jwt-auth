package com.youngjun.auth.application

import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.domain.account.Password
import com.youngjun.auth.domain.account.RawPasswordBuilder
import com.youngjun.auth.support.ApplicationContextTest
import com.youngjun.auth.support.error.AuthException
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
                    val account = AccountBuilder().build()
                    val newPassword = RawPasswordBuilder(value = "newPassword").build()

                    val actual = passwordService.changePassword(account, newPassword)

                    actual.userId shouldBe account.id
                    actual.accessToken.userId shouldBe account.id
                    actual.refreshToken.userId shouldBe account.id
                    passwordEncoder.matches(newPassword.value, account.password) shouldBe true
                }

                test("기존 비밀번호와 동일하면 실패한다.") {
                    val unchangedPassword = RawPasswordBuilder(value = "unchangedPassword").build()
                    val account = AccountBuilder(password = Password.encodedWith(unchangedPassword, passwordEncoder)).build()

                    shouldThrow<AuthException> { passwordService.changePassword(account, unchangedPassword) }
                        .errorType shouldBe ACCOUNT_UNCHANGED_PASSWORD
                }
            }
        },
    )
