package com.youngjun.auth.application

import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.domain.account.AccountRepository
import com.youngjun.auth.domain.account.EmailAddressBuilder
import com.youngjun.auth.domain.support.minutes
import com.youngjun.auth.domain.support.seconds
import com.youngjun.auth.domain.verificationCode.VerificationCodeRepository
import com.youngjun.auth.domain.verificationCode.generateVerificationCode
import com.youngjun.auth.support.ApplicationContextTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DUPLICATE
import com.youngjun.auth.support.error.ErrorType.VERIFICATION_CODE_LIMIT_EXCEEDED
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe

@ApplicationContextTest
class VerificationCodeServiceTest(
    private val verificationCodeService: VerificationCodeService,
    private val verificationCodeRepository: VerificationCodeRepository,
    private val accountRepository: AccountRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("인증 코드 생성") {
                test("성공") {
                    val emailAddress = EmailAddressBuilder().build()

                    val actual = verificationCodeService.generate(emailAddress)

                    actual.emailAddress shouldBe emailAddress
                    actual.code.value.all { it.isDigit() } shouldBe true
                    actual.code.value.length shouldBe 6
                }

                test("가입된 이메일 주소가 존재하면 실패한다.") {
                    val emailAddress = EmailAddressBuilder().build()
                    accountRepository.save(AccountBuilder(emailAddress = emailAddress).build())

                    shouldThrow<AuthException> { verificationCodeService.generate(emailAddress) }
                        .errorType shouldBe ACCOUNT_DUPLICATE
                }

                test("10분 내 발급된 인증 코드가 5개 이상이면 실패한다.") {
                    val emailAddress = EmailAddressBuilder().build()
                    val verificationCodes =
                        verificationCodeRepository.saveAll(List(5) { generateVerificationCode(emailAddress = emailAddress) })

                    shouldThrow<AuthException> {
                        verificationCodeService.generate(emailAddress, verificationCodes.first().createdAt + 10.minutes - 1.seconds)
                    }.errorType shouldBe VERIFICATION_CODE_LIMIT_EXCEEDED
                }
            }
        },
    )
