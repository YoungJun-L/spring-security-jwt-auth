package com.youngjun.auth.domain.verificationCode

import com.youngjun.auth.infra.db.VerificationCodeJpaRepository
import com.youngjun.auth.support.DomainContextTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.data.repository.findByIdOrNull

@DomainContextTest
class VerificationCodeWriterTest(
    private val verificationCodeWriter: VerificationCodeWriter,
    private val verificationCodeJpaRepository: VerificationCodeJpaRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("저장") {
                test("성공") {
                    val verificationCode = generateVerificationCode()

                    verificationCodeWriter.write(verificationCode)

                    verificationCodeJpaRepository.findByIdOrNull(verificationCode.id)!!.code shouldBe verificationCode.code
                }
            }
        },
    )
