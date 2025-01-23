package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.DomainTest
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@DomainTest
class RefreshTokenDetailsTest :
    FunSpec(
        {
            context("refreshToken 상태 검증") {
                test("이용 가능") {
                    val refreshTokenDetails = RefreshTokenDetailsBuilder(status = TokenStatus.ENABLED).build()
                    shouldNotThrow<AuthException> { refreshTokenDetails.verify() }
                }

                test("만료됨") {
                    val refreshTokenDetails = RefreshTokenDetailsBuilder(status = TokenStatus.EXPIRED).build()
                    shouldThrow<AuthException> { refreshTokenDetails.verify() }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }
            }
        },
    )
