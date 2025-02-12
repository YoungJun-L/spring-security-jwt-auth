package com.youngjun.auth.application

import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.domain.account.AccountStatus
import com.youngjun.auth.domain.account.EmailBuilder
import com.youngjun.auth.domain.token.RefreshTokenBuilder
import com.youngjun.auth.domain.token.TokenStatus
import com.youngjun.auth.infra.db.AccountJpaRepository
import com.youngjun.auth.infra.db.RefreshTokenJpaRepository
import com.youngjun.auth.support.ApplicationTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DUPLICATE_ERROR
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder

@ApplicationTest
class AccountServiceTest(
    private val accountService: AccountService,
    private val passwordEncoder: PasswordEncoder,
    private val accountJpaRepository: AccountJpaRepository,
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("유저 조회") {
                test("성공") {
                    val account = accountJpaRepository.save(AccountBuilder().build())

                    val actual = accountService.loadUserByUsername(account.email.value)

                    actual.email shouldBe account.email
                }

                test("회원이 존재하지 않으면 실패한다.") {
                    shouldThrow<UsernameNotFoundException> { accountService.loadUserByUsername("example@youngjun.com") }
                }
            }

            context("회원 가입") {
                test("성공") {
                    val email = EmailBuilder().build()
                    val password = "password123!"

                    val actual = accountService.register(email, password)

                    actual.email shouldBe email
                }

                test("비밀번호는 인코딩된다.") {
                    val email = EmailBuilder().build()
                    val password = "password123!"

                    val actual = accountService.register(email, password)

                    passwordEncoder.matches(password, actual.password) shouldBe true
                }

                test("동일한 아이디로 가입하면 실패한다.") {
                    val email = EmailBuilder().build()
                    accountJpaRepository.save(AccountBuilder(email = email).build())

                    shouldThrow<AuthException> { accountService.register(email, "password123!") }
                        .errorType shouldBe ACCOUNT_DUPLICATE_ERROR
                }
            }

            context("로그아웃") {
                test("성공") {
                    val account = accountJpaRepository.save(AccountBuilder().build())

                    val actual = accountService.logout(account)

                    actual.id shouldBe account.id
                    actual.status shouldBe AccountStatus.LOGOUT
                }

                test("refreshToken 이 만료된다.") {
                    val account = accountJpaRepository.save(AccountBuilder().build())
                    val refreshToken = refreshTokenJpaRepository.save(RefreshTokenBuilder(account.id).build())

                    accountService.logout(account)

                    refreshTokenJpaRepository.findByIdOrNull(refreshToken.id)!!.status shouldBe TokenStatus.EXPIRED
                }
            }

            context("로그인") {
                test("성공") {
                    val account = accountJpaRepository.save(AccountBuilder().build())

                    val actual = accountService.login(account)

                    actual.id shouldBe account.id
                    actual.status shouldBe AccountStatus.ENABLED
                }
            }

            context("비밀번호 변경") {
                test("성공") {
                    val account = accountJpaRepository.save(AccountBuilder().build())
                    val newPassword = "newPassword"

                    val actual = accountService.changePassword(account, newPassword)

                    passwordEncoder.matches(newPassword, actual.password) shouldBe true
                }
            }
        },
    )
