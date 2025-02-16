package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EmailAddressBuilder
import com.youngjun.auth.infra.db.VerificationCodeJpaRepository
import com.youngjun.auth.support.DomainContextTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.VERIFICATION_CODE_LIMIT_EXCEEDED
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe

@DomainContextTest
class VerificationCodeReaderTest(
    private val verificationCodeReader: VerificationCodeReader,
    private val verificationCodeJpaRepository: VerificationCodeJpaRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("허용된 발급 수 검증") {
                test("10분 내 발급된 인증 코드가 5개 미만인 경우") {
                    val emailAddress = EmailAddressBuilder().build()
                    verificationCodeJpaRepository.saveAll(List(4) { VerificationCode.generate(emailAddress = emailAddress) })

                    shouldNotThrow<AuthException> { verificationCodeReader.validateRequestLimitExceeded(emailAddress) }
                }

                test("10분 내 발급된 인증 코드가 5개 이상인 경우") {
                    val emailAddress = EmailAddressBuilder().build()
                    verificationCodeJpaRepository.saveAll(List(5) { VerificationCode.generate(emailAddress = emailAddress) })

                    shouldThrow<AuthException> { verificationCodeReader.validateRequestLimitExceeded(emailAddress) }
                        .errorType shouldBe VERIFICATION_CODE_LIMIT_EXCEEDED
                }
            }
        },
    )
