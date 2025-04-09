package com.youngjun.auth.domain.account

import com.youngjun.auth.support.DomainContextTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.data.repository.findByIdOrNull

@DomainContextTest
class AccountWriterTest(
    private val accountWriter: AccountWriter,
    private val accountRepository: AccountRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("저장") {
                test("성공") {
                    val account = AccountBuilder().build()

                    accountWriter.write(account)

                    accountRepository.findByIdOrNull(account.id)!!.emailAddress shouldBe account.emailAddress
                }
            }
        },
    )
