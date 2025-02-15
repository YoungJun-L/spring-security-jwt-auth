package com.youngjun.auth.domain.account

import com.youngjun.auth.infra.db.AccountJpaRepository
import com.youngjun.auth.support.DomainContextTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe

@DomainContextTest
class AccountWriterTest(
    private val accountWriter: AccountWriter,
    private val accountJpaRepository: AccountJpaRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("기존 계정 저장") {
                test("성공") {
                    val account = accountJpaRepository.save(AccountBuilder().build())

                    val actual = accountWriter.write(account)

                    actual.email shouldBe account.email
                }
            }
        },
    )
