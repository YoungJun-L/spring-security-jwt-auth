package com.youngjun.auth.domain.account

import com.youngjun.auth.infra.db.AccountJpaRepository
import com.youngjun.auth.support.DomainTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DUPLICATE_ERROR
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe

@DomainTest
class AccountWriterTest(
    private val accountWriter: AccountWriter,
    private val accountJpaRepository: AccountJpaRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("새 계정 저장") {
                test("성공") {
                    val newAccount = NewAccountBuilder().build()

                    val actual = accountWriter.write(newAccount)

                    actual.username shouldBe newAccount.username
                }

                test("동일한 아이디로 저장하면 실패한다.") {
                    val account = accountJpaRepository.save(AccountBuilder().build())

                    shouldThrow<AuthException> { accountWriter.write(NewAccountBuilder(username = account.username).build()) }
                        .errorType shouldBe ACCOUNT_DUPLICATE_ERROR
                }
            }

            context("기존 계정 저장") {
                test("성공") {
                    val account = accountJpaRepository.save(AccountBuilder().build())

                    val actual = accountWriter.write(account)

                    actual.username shouldBe account.username
                }
            }
        },
    )
