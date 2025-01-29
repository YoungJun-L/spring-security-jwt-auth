package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.domain.account.NewAccountBuilder
import com.youngjun.auth.core.support.ApplicationTest
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.ACCOUNT_DUPLICATE_ERROR
import com.youngjun.auth.storage.db.core.account.AccountEntityBuilder
import com.youngjun.auth.storage.db.core.account.AccountJpaRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder

@ApplicationTest
class AccountServiceTest(
    private val accountService: AccountService,
    private val passwordEncoder: PasswordEncoder,
    private val accountJpaRepository: AccountJpaRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("유저 조회") {
                test("성공") {
                    val username = "username123"
                    accountJpaRepository.save(AccountEntityBuilder(username = username).build())

                    val actual = accountService.loadUserByUsername(username)

                    actual.username shouldBe username
                }

                test("회원이 존재하지 않으면 실패한다.") {
                    shouldThrow<UsernameNotFoundException> { accountService.loadUserByUsername("username123") }
                }
            }

            context("회원 가입") {
                test("성공") {
                    val newAccount = NewAccountBuilder().build()

                    val actual = accountService.register(newAccount)

                    actual.username shouldBe newAccount.username
                }

                test("비밀번호는 인코딩된다.") {
                    val newAccount = NewAccountBuilder().build()

                    val actual = accountService.register(newAccount)

                    passwordEncoder.matches(newAccount.password, actual.password) shouldBe true
                }

                test("동일한 아이디로 가입하면 실패한다.") {
                    val username = "username123"
                    accountJpaRepository.save(AccountEntityBuilder(username = username).build())

                    shouldThrow<AuthException> { accountService.register(NewAccountBuilder(username = username).build()) }
                        .errorType shouldBe ACCOUNT_DUPLICATE_ERROR
                }
            }
        },
    )
