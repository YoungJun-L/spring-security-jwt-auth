package com.youngjun.auth.domain.token

import com.youngjun.auth.support.DomainTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@DomainTest
class RefreshTokenTest :
    FunSpec(
        {
            context("refreshToken 상태 검증") {
                test("이용 가능") {
                    val refreshToken = RefreshTokenBuilder(status = TokenStatus.ENABLED).build()

                    shouldNotThrow<AuthException> { refreshToken.verify() }
                }

                test("만료됨") {
                    val refreshToken = RefreshTokenBuilder(status = TokenStatus.EXPIRED).build()

                    shouldThrow<AuthException> { refreshToken.verify() }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }
            }
        },
    )
