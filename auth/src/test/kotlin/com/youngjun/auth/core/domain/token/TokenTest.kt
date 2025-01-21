package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class TokenTest :
    FunSpec(
        {
            context("토큰 상태 검증") {
                test("이용 가능") {
                    val token = TokenBuilder(status = TokenStatus.ENABLED).build()
                    shouldNotThrow<AuthException> { token.verify() }
                }

                test("만료됨") {
                    val token = TokenBuilder(status = TokenStatus.EXPIRED).build()
                    shouldThrow<AuthException> { token.verify() }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }
            }
        },
    )
