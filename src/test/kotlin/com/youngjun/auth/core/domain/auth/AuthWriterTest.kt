package com.youngjun.auth.core.domain.auth

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.AUTH_DUPLICATE_ERROR
import com.youngjun.auth.core.domain.support.DomainTest
import com.youngjun.auth.storage.db.core.auth.AuthRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

@DomainTest
class AuthWriterTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val authRepository = mockk<AuthRepository>()
            val authWriter = AuthWriter(authRepository)

            context("저장") {
                test("성공") {
                    val newAuth = NewAuthBuilder().build()
                    val auth = AuthBuilder(username = newAuth.username).build()
                    every { authRepository.existsByUsername(newAuth.username) } returns false
                    every { authRepository.write(newAuth) } returns auth

                    val actual = authWriter.write(newAuth)

                    actual.username shouldBe newAuth.username
                }

                test("동일한 이름으로 저장하면 실패한다.") {
                    val username = "username123"
                    every { authRepository.existsByUsername(username) } returns true

                    shouldThrow<AuthException> { authWriter.write(NewAuthBuilder(username = username).build()) }
                        .errorType shouldBe AUTH_DUPLICATE_ERROR
                }
            }
        },
    )
