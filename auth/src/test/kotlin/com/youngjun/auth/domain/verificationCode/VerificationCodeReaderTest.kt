package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EmailAddressBuilder
import com.youngjun.auth.domain.support.minutes
import com.youngjun.auth.domain.support.seconds
import com.youngjun.auth.infra.db.VerificationCodeJpaRepository
import com.youngjun.auth.support.DomainContextTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.VERIFICATION_CODE_LIMIT_EXCEEDED
import com.youngjun.auth.support.error.ErrorType.VERIFICATION_CODE_NOT_FOUND
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
                    val now =
                        verificationCodeJpaRepository
                            .saveAll(List(4) { generateVerificationCode(emailAddress = emailAddress) })
                            .first()
                            .createdAt + 10.minutes - 1.seconds

                    shouldNotThrow<AuthException> { verificationCodeReader.checkRecentSavesExceeded(emailAddress, now) }
                }

                test("10분 내 발급된 인증 코드가 5개 이상인 경우") {
                    val emailAddress = EmailAddressBuilder().build()
                    val now =
                        verificationCodeJpaRepository
                            .saveAll(List(5) { generateVerificationCode(emailAddress = emailAddress) })
                            .first()
                            .createdAt + 10.minutes - 1.seconds

                    shouldThrow<AuthException> { verificationCodeReader.checkRecentSavesExceeded(emailAddress, now) }
                        .errorType shouldBe VERIFICATION_CODE_LIMIT_EXCEEDED
                }
            }

            context("최근 발급 건 조회") {
                test("성공") {
                    val emailAddress = EmailAddressBuilder().build()
                    verificationCodeJpaRepository.save(generateVerificationCode(emailAddress = emailAddress))
                    val verificationCode = verificationCodeJpaRepository.save(generateVerificationCode(emailAddress = emailAddress))

                    val actual = verificationCodeReader.readLatest(emailAddress)

                    actual.code shouldBe verificationCode.code
                }

                test("존재하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { verificationCodeReader.readLatest(EmailAddressBuilder().build()) }
                        .errorType shouldBe VERIFICATION_CODE_NOT_FOUND
                }
            }
        },
    )
