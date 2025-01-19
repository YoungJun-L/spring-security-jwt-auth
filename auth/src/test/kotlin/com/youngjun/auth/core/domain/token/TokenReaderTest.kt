package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.DomainTest
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_NOT_FOUND_ERROR
import com.youngjun.auth.storage.db.core.token.TokenRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

@DomainTest
class TokenReaderTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val tokenRepository = mockk<TokenRepository>()
            val tokenReader = TokenReader(tokenRepository)

            context("토큰 조회") {
                test("성공") {
                    val refreshToken = RefreshTokenBuilder().build()
                    every { tokenRepository.read(any()) } returns TokenBuilder(refreshToken = refreshToken).build()

                    val actual = tokenReader.read(refreshToken)

                    actual.refreshToken.value shouldBe refreshToken.value
                }

                test("토큰이 존재하지 않는 경우 실패한다.") {
                    every { tokenRepository.read(any()) } returns null

                    shouldThrow<AuthException> { tokenReader.read(RefreshTokenBuilder().build()) }
                        .errorType shouldBe TOKEN_NOT_FOUND_ERROR
                }
            }
        },
    )
