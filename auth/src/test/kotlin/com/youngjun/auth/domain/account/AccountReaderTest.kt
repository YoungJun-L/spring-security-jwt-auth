package com.youngjun.auth.domain.account

import com.youngjun.auth.infra.db.AccountJpaRepository
import com.youngjun.auth.support.DomainContextTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DISABLED
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DUPLICATE
import com.youngjun.auth.support.error.ErrorType.UNAUTHORIZED
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.security.core.userdetails.UsernameNotFoundException

@DomainContextTest
class AccountReaderTest(
    private val accountReader: AccountReader,
    private val accountJpaRepository: AccountJpaRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("조회") {
                test("성공") {
                    val account = accountJpaRepository.save(AccountBuilder().build())

                    val actual = accountReader.read(account.emailAddress)

                    actual.id shouldBe account.id
                }

                test("존재하지 않으면 실패한다.") {
                    shouldThrow<UsernameNotFoundException> { accountReader.read(EmailAddressBuilder().build()) }
                }
            }

            context("이용 가능한 유저 조회") {
                test("성공") {
                    val account = accountJpaRepository.save(AccountBuilder(status = AccountStatus.ENABLED).build())

                    val actual = accountReader.readEnabled(account.id)

                    actual.id shouldBe account.id
                }

                test("존재하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { accountReader.readEnabled(1L) }
                        .errorType shouldBe UNAUTHORIZED
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val account = accountJpaRepository.save(AccountBuilder(status = AccountStatus.DISABLED).build())

                    shouldThrow<AuthException> { accountReader.readEnabled(account.id) }
                        .errorType shouldBe ACCOUNT_DISABLED
                }
            }

            context("중복 이메일 주소 검증") {
                test("중복되지 않는 경우") {
                    shouldNotThrow<AuthException> { accountReader.checkExists(EmailAddressBuilder().build()) }
                }

                test("중복되는 경우") {
                    val emailAddress = EmailAddressBuilder().build()
                    accountJpaRepository.save(AccountBuilder(emailAddress = emailAddress).build())

                    shouldThrow<AuthException> { accountReader.checkExists(emailAddress) }
                        .errorType shouldBe ACCOUNT_DUPLICATE
                }
            }
        },
    )
