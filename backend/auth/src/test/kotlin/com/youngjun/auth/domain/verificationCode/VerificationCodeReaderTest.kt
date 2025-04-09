package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.domain.account.EMAIL_ADDRESS
import com.youngjun.auth.domain.support.minutes
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
    private val verificationCodeRepository: VerificationCodeRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("허용된 발급 수 검증") {
                test("10분 내 발급된 인증 코드가 5개 미만인 경우") {
                    val now =
                        verificationCodeRepository
                            .saveAll(List(4) { generateVerificationCode(emailAddress = EMAIL_ADDRESS) })
                            .first()
                            .createdAt + 10.minutes

                    shouldNotThrow<AuthException> { verificationCodeReader.checkRecentSavesExceeded(EMAIL_ADDRESS, now) }
                }

                test("10분 내 발급된 인증 코드가 5개 이상인 경우") {
                    val now =
                        verificationCodeRepository
                            .saveAll(List(5) { generateVerificationCode(emailAddress = EMAIL_ADDRESS) })
                            .first()
                            .createdAt + 10.minutes

                    shouldThrow<AuthException> { verificationCodeReader.checkRecentSavesExceeded(EMAIL_ADDRESS, now) }
                        .errorType shouldBe VERIFICATION_CODE_LIMIT_EXCEEDED
                }
            }

            context("최근 발급 건 조회") {
                test("성공") {
                    verificationCodeRepository.save(generateVerificationCode(emailAddress = EMAIL_ADDRESS))
                    val verificationCode = verificationCodeRepository.save(generateVerificationCode(emailAddress = EMAIL_ADDRESS))

                    val actual = verificationCodeReader.readLatest(EMAIL_ADDRESS)

                    actual.code shouldBe verificationCode.code
                }

                test("존재하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { verificationCodeReader.readLatest(EMAIL_ADDRESS) }
                        .errorType shouldBe VERIFICATION_CODE_NOT_FOUND
                }
            }
        },
    )
