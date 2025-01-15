package com.youngjun.auth.core.domain.account

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.ACCOUNT_DUPLICATE_ERROR
import com.youngjun.auth.core.domain.support.DomainTest
import com.youngjun.auth.storage.db.core.account.AccountRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

@DomainTest
class AccountWriterTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val accountRepository = mockk<AccountRepository>()
            val accountWriter = AccountWriter(accountRepository)

            context("저장") {
                test("성공") {
                    val newAccount = NewAccountBuilder().build()
                    every { accountRepository.existsByUsername(any()) } returns false
                    every { accountRepository.write(any()) } returns AccountBuilder(username = newAccount.username).build()

                    val actual = accountWriter.write(newAccount)

                    actual.username shouldBe newAccount.username
                }

                test("동일한 이름으로 저장하면 실패한다.") {
                    every { accountRepository.existsByUsername(any()) } returns true

                    shouldThrow<AuthException> { accountWriter.write(NewAccountBuilder(username = "username123").build()) }
                        .errorType shouldBe ACCOUNT_DUPLICATE_ERROR
                }
            }
        },
    )
