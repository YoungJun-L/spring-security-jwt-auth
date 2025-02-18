package com.youngjun.auth.application

import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.domain.account.AccountStatus
import com.youngjun.auth.domain.account.EmailAddressBuilder
import com.youngjun.auth.domain.account.RawPasswordBuilder
import com.youngjun.auth.domain.support.minutes
import com.youngjun.auth.domain.token.RefreshTokenBuilder
import com.youngjun.auth.domain.token.TokenStatus
import com.youngjun.auth.domain.verificationCode.RawVerificationCodeBuilder
import com.youngjun.auth.domain.verificationCode.generateRawVerificationCodeExcluding
import com.youngjun.auth.domain.verificationCode.generateVerificationCode
import com.youngjun.auth.infra.db.AccountJpaRepository
import com.youngjun.auth.infra.db.RefreshTokenJpaRepository
import com.youngjun.auth.infra.db.VerificationCodeJpaRepository
import com.youngjun.auth.support.ApplicationContextTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DUPLICATE
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder

@ApplicationContextTest
class AccountServiceTest(
    private val accountService: AccountService,
    private val passwordEncoder: PasswordEncoder,
    private val accountJpaRepository: AccountJpaRepository,
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
    private val verificationCodeJpaRepository: VerificationCodeJpaRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("유저 조회") {
                test("성공") {
                    val account = accountJpaRepository.save(AccountBuilder().build())

                    val actual = accountService.loadUserByUsername(account.emailAddress.value)

                    actual.emailAddress shouldBe account.emailAddress
                }

                test("존재하지 않으면 실패한다.") {
                    shouldThrow<UsernameNotFoundException> { accountService.loadUserByUsername("example@youngjun.com") }
                }
            }

            context("회원 가입") {
                test("성공") {
                    val emailAddress = EmailAddressBuilder().build()
                    val verificationCode = verificationCodeJpaRepository.save(generateVerificationCode(emailAddress))

                    val actual =
                        accountService.register(
                            emailAddress,
                            RawPasswordBuilder().build(),
                            RawVerificationCodeBuilder(verificationCode.code).build(),
                        )

                    actual.emailAddress shouldBe emailAddress
                }

                test("비밀번호는 인코딩된다.") {
                    val emailAddress = EmailAddressBuilder().build()
                    val rawPassword = RawPasswordBuilder().build()
                    val verificationCode = verificationCodeJpaRepository.save(generateVerificationCode(emailAddress))

                    val actual =
                        accountService.register(
                            emailAddress,
                            rawPassword,
                            RawVerificationCodeBuilder(verificationCode.code).build(),
                        )

                    passwordEncoder.matches(rawPassword.value, actual.password) shouldBe true
                }

                test("중복된 이메일 주소가 존재하면 실패한다.") {
                    val emailAddress = EmailAddressBuilder().build()
                    accountJpaRepository.save(AccountBuilder(emailAddress = emailAddress).build())

                    shouldThrow<AuthException> {
                        accountService.register(emailAddress, RawPasswordBuilder().build(), RawVerificationCodeBuilder().build())
                    }.errorType shouldBe ACCOUNT_DUPLICATE
                }

                test("인증 코드가 존재하지 않으면 실패한다.") {
                    shouldThrow<AuthException> {
                        accountService.register(
                            EmailAddressBuilder().build(),
                            RawPasswordBuilder().build(),
                            RawVerificationCodeBuilder().build(),
                        )
                    }.errorType shouldBe ErrorType.VERIFICATION_CODE_NOT_FOUND
                }

                test("인증 코드가 일치하지 않으면 실패한다.") {
                    val emailAddress = EmailAddressBuilder().build()
                    val verificationCode = verificationCodeJpaRepository.save(generateVerificationCode(emailAddress))

                    shouldThrow<AuthException> {
                        accountService.register(
                            emailAddress,
                            RawPasswordBuilder().build(),
                            generateRawVerificationCodeExcluding(verificationCode),
                        )
                    }.errorType shouldBe ErrorType.VERIFICATION_CODE_MISMATCHED
                }

                test("인증 코드 유효 기간이 지났으면 실패한다.") {
                    val emailAddress = EmailAddressBuilder().build()
                    val verificationCode = verificationCodeJpaRepository.save(generateVerificationCode(emailAddress))

                    shouldThrow<AuthException> {
                        accountService.register(
                            emailAddress,
                            RawPasswordBuilder().build(),
                            RawVerificationCodeBuilder(verificationCode.code).build(),
                            verificationCode.createdAt + 10.minutes,
                        )
                    }.errorType shouldBe ErrorType.VERIFICATION_CODE_EXPIRED
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
                    val newPassword = RawPasswordBuilder(value = "newPassword").build()

                    val actual = accountService.changePassword(account, newPassword)

                    passwordEncoder.matches(newPassword.value, actual.password) shouldBe true
                }
            }
        },
    )
