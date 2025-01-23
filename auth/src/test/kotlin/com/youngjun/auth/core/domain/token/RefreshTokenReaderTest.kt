package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.DomainTest
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_NOT_FOUND_ERROR
import com.youngjun.auth.storage.db.core.token.RefreshTokenEntityBuilder
import com.youngjun.auth.storage.db.core.token.RefreshTokenJpaRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe

@DomainTest
class RefreshTokenReaderTest(
    private val refreshTokenReader: RefreshTokenReader,
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("토큰 조회") {
                test("성공") {
                    val refreshTokenEntity = refreshTokenJpaRepository.save(RefreshTokenEntityBuilder().build())

                    val actual = refreshTokenReader.read(RefreshToken(refreshTokenEntity.refreshToken))

                    actual.refreshToken.value shouldBe refreshTokenEntity.refreshToken
                }

                test("토큰이 존재하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { refreshTokenReader.read(RefreshToken("")) }
                        .errorType shouldBe TOKEN_NOT_FOUND_ERROR
                }
            }
        },
    )
