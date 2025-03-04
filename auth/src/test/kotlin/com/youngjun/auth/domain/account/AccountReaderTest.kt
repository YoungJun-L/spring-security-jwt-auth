package com.youngjun.auth.domain.account

import com.youngjun.auth.support.DomainContextTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DUPLICATE
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_NOT_FOUND
import com.youngjun.auth.support.error.ErrorType.UNAUTHORIZED
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe

@DomainContextTest
class AccountReaderTest(
    private val accountReader: AccountReader,
    private val accountRepository: AccountRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("이메일 주소로 조회") {
                test("성공") {
                    val account = accountRepository.save(AccountBuilder().build())

                    val actual = accountReader.read(account.emailAddress)

                    actual.id shouldBe account.id
                }

                test("존재하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { accountReader.read(EMAIL_ADDRESS) }
                        .errorType shouldBe ACCOUNT_NOT_FOUND
                }
            }

            context("조회") {
                test("성공") {
                    val account = accountRepository.save(AccountBuilder(status = AccountStatus.ENABLED).build())

                    val actual = accountReader.read(account.id)

                    actual.id shouldBe account.id
                }

                test("존재하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { accountReader.read(1L) }
                        .errorType shouldBe UNAUTHORIZED
                }
            }

            context("중복 이메일 주소 검증") {
                test("중복되지 않는 경우") {
                    shouldNotThrow<AuthException> { accountReader.checkNotDuplicate(EMAIL_ADDRESS) }
                }

                test("중복되는 경우") {
                    accountRepository.save(AccountBuilder(emailAddress = EMAIL_ADDRESS).build())

                    shouldThrow<AuthException> { accountReader.checkNotDuplicate(EMAIL_ADDRESS) }
                        .errorType shouldBe ACCOUNT_DUPLICATE
                }
            }
        },
    )
