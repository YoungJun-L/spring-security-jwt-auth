package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.DomainTest
import com.youngjun.auth.storage.db.core.token.TokenRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk

@DomainTest
class TokenWriterTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val tokenRepository = mockk<TokenRepository>()
            val tokenWriter = TokenWriter(tokenRepository)

            context("토큰 교체") {
                test("성공") {
                    val newToken = TokenBuilder().build()
                    every { tokenRepository.delete(any()) } just Runs
                    every { tokenRepository.update(any()) } returns newToken

                    val actual = tokenWriter.update(TokenPairBuilder().build())

                    actual.refreshToken shouldBe newToken.refreshToken
                }
            }
        },
    )
