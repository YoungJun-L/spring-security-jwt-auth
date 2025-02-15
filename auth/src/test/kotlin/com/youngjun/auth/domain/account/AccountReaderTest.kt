package com.youngjun.auth.domain.account

import com.youngjun.auth.infra.db.AccountJpaRepository
import com.youngjun.auth.support.DomainContextTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DISABLED_ERROR
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DUPLICATE_ERROR
import com.youngjun.auth.support.error.ErrorType.UNAUTHORIZED_ERROR
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

            context("회원 조회") {
                test("성공") {
                    val account = accountJpaRepository.save(AccountBuilder().build())

                    val actual = accountReader.read(account.email)

                    actual.id shouldBe account.id
                }

                test("회원이 존재하지 않으면 실패한다.") {
                    shouldThrow<UsernameNotFoundException> { accountReader.read(EmailBuilder().build()) }
                }
            }

            context("이용 가능한 회원 조회") {
                test("성공") {
                    val account = accountJpaRepository.save(AccountBuilder(status = AccountStatus.ENABLED).build())

                    val actual = accountReader.readEnabled(account.id)

                    actual.id shouldBe account.id
                }

                test("회원이 존재하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { accountReader.readEnabled(1L) }
                        .errorType shouldBe UNAUTHORIZED_ERROR
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val account = accountJpaRepository.save(AccountBuilder(status = AccountStatus.DISABLED).build())

                    shouldThrow<AuthException> { accountReader.readEnabled(account.id) }
                        .errorType shouldBe ACCOUNT_DISABLED_ERROR
                }
            }

            context("중복 아이디 검증") {
                test("성공") {
                    shouldNotThrow<AuthException> { accountReader.validateUniqueEmail(EmailBuilder().build()) }
                }

                test("중복 아이디가 존재하면 실패한다.") {
                    val email = EmailBuilder().build()
                    accountJpaRepository.save(AccountBuilder(email = email).build())

                    shouldThrow<AuthException> { accountReader.validateUniqueEmail(email) }
                        .errorType shouldBe ACCOUNT_DUPLICATE_ERROR
                }
            }
        },
    )
