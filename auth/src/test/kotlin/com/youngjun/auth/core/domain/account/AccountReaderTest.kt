package com.youngjun.auth.core.domain.account

import com.youngjun.auth.core.support.DomainTest
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.UNAUTHORIZED_ERROR
import com.youngjun.auth.storage.db.core.account.AccountRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.security.core.userdetails.UsernameNotFoundException

@DomainTest
class AccountReaderTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val accountRepository = mockk<AccountRepository>()
            val accountReader = AccountReader(accountRepository)

            context("회원 조회") {
                test("성공") {
                    val account = AccountBuilder().build()
                    every { accountRepository.read(any<String>()) } returns account

                    val actual = accountReader.read(account.username)

                    actual.id shouldBe account.id
                }

                test("회원이 존재하지 않는 경우 실패한다.") {
                    every { accountRepository.read(any<String>()) } returns null

                    shouldThrow<UsernameNotFoundException> { accountReader.read("username123") }
                }
            }

            context("이용 가능한 회원 조회") {
                test("성공") {
                    val account = AccountBuilder(status = AccountStatus.ENABLED).build()
                    every { accountRepository.read(any<Long>()) } returns account

                    val actual = accountReader.readEnabled(account.id)

                    actual.id shouldBe account.id
                }

                test("회원이 존재하지 않는 경우 실패한다.") {
                    every { accountRepository.read(any<Long>()) } returns null

                    shouldThrow<AuthException> { accountReader.readEnabled(1L) }.errorType shouldBe UNAUTHORIZED_ERROR
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val account = AccountBuilder(status = AccountStatus.DISABLED).build()
                    every { accountRepository.read(any<Long>()) } returns account

                    shouldThrow<AuthException> { accountReader.readEnabled(account.id) }
                }
            }
        },
    )
