package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.DomainTest
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_NOT_FOUND_ERROR
import com.youngjun.auth.storage.db.core.token.TokenEntityBuilder
import com.youngjun.auth.storage.db.core.token.TokenJpaRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe

@DomainTest
class TokenReaderTest(
    private val tokenReader: TokenReader,
    private val tokenJpaRepository: TokenJpaRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("토큰 조회") {
                test("성공") {
                    val tokenEntity = tokenJpaRepository.save(TokenEntityBuilder().build())

                    val actual = tokenReader.read(tokenEntity.refreshToken)

                    actual.refreshToken shouldBe tokenEntity.refreshToken
                }

                test("토큰이 존재하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { tokenReader.read("") }
                        .errorType shouldBe TOKEN_NOT_FOUND_ERROR
                }
            }
        },
    )
