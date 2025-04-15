package com.youngjun.auth.application

import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.domain.account.AccountRepository
import com.youngjun.auth.domain.account.AccountStatus
import com.youngjun.auth.domain.account.EMAIL_ADDRESS
import com.youngjun.auth.domain.account.PROFILE
import com.youngjun.auth.domain.account.RAW_PASSWORD
import com.youngjun.auth.domain.support.minutes
import com.youngjun.auth.domain.token.RefreshTokenBuilder
import com.youngjun.auth.domain.token.RefreshTokenRepository
import com.youngjun.auth.domain.token.TokenStatus
import com.youngjun.auth.domain.verificationCode.RAW_VERIFICATION_CODE
import com.youngjun.auth.domain.verificationCode.VerificationCodeRepository
import com.youngjun.auth.domain.verificationCode.generateRawVerificationCodeExcluding
import com.youngjun.auth.domain.verificationCode.generateVerificationCode
import com.youngjun.auth.support.ApplicationContextTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DUPLICATE
import com.youngjun.auth.support.error.ErrorType.VERIFICATION_CODE_EXPIRED
import com.youngjun.auth.support.error.ErrorType.VERIFICATION_CODE_MISMATCHED
import com.youngjun.auth.support.error.ErrorType.VERIFICATION_CODE_NOT_FOUND
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
    private val accountRepository: AccountRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val verificationCodeRepository: VerificationCodeRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("유저 조회") {
                test("성공") {
                    val account = accountRepository.save(AccountBuilder().build())

                    val actual = accountService.loadUserByUsername(account.username)

                    actual.emailAddress shouldBe account.emailAddress
                }

                test("존재하지 않으면 실패한다.") {
                    shouldThrow<UsernameNotFoundException> { accountService.loadUserByUsername(EMAIL_ADDRESS.value) }
                }
            }

            context("회원 가입") {
                test("성공") {
                    val verificationCode = verificationCodeRepository.save(generateVerificationCode(EMAIL_ADDRESS))

                    val actual = accountService.register(EMAIL_ADDRESS, RAW_PASSWORD, PROFILE, verificationCode.code)

                    actual.emailAddress shouldBe EMAIL_ADDRESS
                }

                test("비밀번호는 인코딩된다.") {
                    val verificationCode = verificationCodeRepository.save(generateVerificationCode(EMAIL_ADDRESS))

                    val actual = accountService.register(EMAIL_ADDRESS, RAW_PASSWORD, PROFILE, verificationCode.code)

                    passwordEncoder.matches(RAW_PASSWORD.value, actual.password) shouldBe true
                }

                test("중복된 이메일 주소가 존재하면 실패한다.") {
                    accountRepository.save(AccountBuilder(emailAddress = EMAIL_ADDRESS).build())

                    shouldThrow<AuthException> {
                        accountService.register(EMAIL_ADDRESS, RAW_PASSWORD, PROFILE, RAW_VERIFICATION_CODE)
                    }.errorType shouldBe ACCOUNT_DUPLICATE
                }

                test("인증 코드가 존재하지 않으면 실패한다.") {
                    shouldThrow<AuthException> {
                        accountService.register(EMAIL_ADDRESS, RAW_PASSWORD, PROFILE, RAW_VERIFICATION_CODE)
                    }.errorType shouldBe VERIFICATION_CODE_NOT_FOUND
                }

                test("인증 코드가 일치하지 않으면 실패한다.") {
                    val verificationCode = verificationCodeRepository.save(generateVerificationCode(EMAIL_ADDRESS))

                    shouldThrow<AuthException> {
                        accountService.register(
                            EMAIL_ADDRESS,
                            RAW_PASSWORD,
                            PROFILE,
                            generateRawVerificationCodeExcluding(verificationCode),
                        )
                    }.errorType shouldBe VERIFICATION_CODE_MISMATCHED
                }

                test("인증 코드 유효 기간이 지났으면 실패한다.") {
                    val verificationCode = verificationCodeRepository.save(generateVerificationCode(EMAIL_ADDRESS))

                    shouldThrow<AuthException> {
                        accountService.register(
                            EMAIL_ADDRESS,
                            RAW_PASSWORD,
                            PROFILE,
                            verificationCode.code,
                            verificationCode.createdAt + 10.minutes,
                        )
                    }.errorType shouldBe VERIFICATION_CODE_EXPIRED
                }
            }

            context("로그아웃") {
                test("성공") {
                    val account = AccountBuilder().build()

                    val actual = accountService.logout(account)

                    actual.id shouldBe account.id
                    actual.status shouldBe AccountStatus.LOGOUT
                }

                test("refreshToken 이 만료된다.") {
                    val account = accountRepository.save(AccountBuilder().build())
                    val refreshToken = refreshTokenRepository.save(RefreshTokenBuilder(userId = account.id).build())

                    accountService.logout(account)

                    refreshTokenRepository.findByIdOrNull(refreshToken.id)!!.status shouldBe TokenStatus.EXPIRED
                }
            }

            context("로그인") {
                test("성공") {
                    val account = AccountBuilder().build()

                    val actual = accountService.login(account)

                    actual.id shouldBe account.id
                    actual.status shouldBe AccountStatus.ENABLED
                }
            }
        },
    )
