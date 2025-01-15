package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.api.support.ApplicationTest
import com.youngjun.auth.core.api.support.VALID_PASSWORD
import com.youngjun.auth.core.domain.account.NewAccountBuilder
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.security.crypto.password.PasswordEncoder

@ApplicationTest
class AccountServiceTest(
    private val accountService: AccountService,
    private val passwordEncoder: PasswordEncoder,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("회원 가입") {
                test("성공") {
                    val newAccount = NewAccountBuilder().build()

                    val actual = accountService.register(newAccount)

                    actual.username shouldBe newAccount.username
                }

                test("비밀번호는 인코딩된다.") {
                    val newAccount = NewAccountBuilder(password = VALID_PASSWORD).build()

                    val actual = accountService.register(newAccount)

                    passwordEncoder.matches(newAccount.password, actual.password) shouldBe true
                }
            }
        },
    )
