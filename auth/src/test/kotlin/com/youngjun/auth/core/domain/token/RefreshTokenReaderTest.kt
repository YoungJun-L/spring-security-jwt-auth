package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.DomainTest
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_INVALID_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_NOT_FOUND_ERROR
import com.youngjun.auth.storage.db.core.token.RefreshTokenEntityBuilder
import com.youngjun.auth.storage.db.core.token.RefreshTokenJpaRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import java.time.Duration

@DomainTest
class RefreshTokenReaderTest(
    private val refreshTokenReader: RefreshTokenReader,
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
    private val secretKeyHolder: SecretKeyHolder,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("이용 가능한 refreshToken 조회") {
                test("성공") {
                    val userId = 1L
                    val refreshToken =
                        RefreshToken(JwtBuilder(secretKey = secretKeyHolder.get(), subject = userId.toString()).build())
                    refreshTokenJpaRepository.save(RefreshTokenEntityBuilder(userId, refreshToken.value).build())

                    val actual = refreshTokenReader.readEnabled(refreshToken)

                    actual.refreshToken shouldBe refreshToken
                }

                test("refreshToken 이 존재하지 않으면 실패한다.") {
                    shouldThrow<AuthException> {
                        refreshTokenReader.readEnabled(RefreshToken(JwtBuilder(secretKey = secretKeyHolder.get()).build()))
                    }.errorType shouldBe TOKEN_NOT_FOUND_ERROR
                }

                test("refreshToken 이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { refreshTokenReader.readEnabled(RefreshToken("INVALID")) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }

                test("refreshToken 의 만료 시간이 지났으면 실패한다.") {
                    val userId = 1L
                    val refreshToken =
                        RefreshToken(
                            JwtBuilder(
                                secretKey = secretKeyHolder.get(),
                                subject = userId.toString(),
                                expiresIn = Duration.ZERO,
                            ).build(),
                        )
                    refreshTokenJpaRepository.save(RefreshTokenEntityBuilder(userId, refreshToken.value).build())

                    shouldThrow<AuthException> { refreshTokenReader.readEnabled(refreshToken) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }

                test("refreshToken 이 만료되었으면 실패한다.") {
                    val userId = 1L
                    val refreshToken =
                        RefreshToken(
                            JwtBuilder(
                                secretKey = secretKeyHolder.get(),
                                subject = userId.toString(),
                            ).build(),
                        )
                    refreshTokenJpaRepository.save(
                        RefreshTokenEntityBuilder(
                            userId,
                            refreshToken.value,
                            TokenStatus.EXPIRED,
                        ).build(),
                    )

                    shouldThrow<AuthException> { refreshTokenReader.readEnabled(refreshToken) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }
            }
        },
    )
