package com.youngjun.auth.security.provider

import com.youngjun.auth.application.TokenService
import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.domain.token.JwtBuilder
import com.youngjun.auth.security.support.error.TypedAuthenticationException
import com.youngjun.auth.security.token.BearerTokenAuthenticationToken
import com.youngjun.auth.support.SecurityTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DISABLED
import com.youngjun.auth.support.error.ErrorType.TOKEN_EXPIRED
import com.youngjun.auth.support.error.ErrorType.TOKEN_INVALID
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

@SecurityTest
class JwtAuthenticationProviderTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val tokenService = mockk<TokenService>()
            val jwtAuthenticationProvider = JwtAuthenticationProvider(tokenService)

            context("JWT 인증") {
                test("성공") {
                    val authentication = BearerTokenAuthenticationToken(JwtBuilder().build())
                    every { tokenService.parse(any()) } returns AccountBuilder().build()

                    val actual = jwtAuthenticationProvider.authenticate(authentication)

                    actual.isAuthenticated shouldBe true
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val authentication = BearerTokenAuthenticationToken(JwtBuilder().build())
                    every { tokenService.parse(any()) } throws AuthException(ACCOUNT_DISABLED)

                    shouldThrow<TypedAuthenticationException> { jwtAuthenticationProvider.authenticate(authentication) }
                        .errorType shouldBe ACCOUNT_DISABLED
                }

                test("accessToken 이 유효하지 않으면 실패한다.") {
                    val authentication = BearerTokenAuthenticationToken(JwtBuilder().build())
                    every { tokenService.parse(any()) } throws AuthException(TOKEN_INVALID)

                    shouldThrow<TypedAuthenticationException> { jwtAuthenticationProvider.authenticate(authentication) }
                        .errorType shouldBe TOKEN_INVALID
                }

                test("accessToken 이 만료되었으면 실패한다.") {
                    val authentication = BearerTokenAuthenticationToken(JwtBuilder().build())
                    every { tokenService.parse(any()) } throws AuthException(TOKEN_EXPIRED)

                    shouldThrow<TypedAuthenticationException> { jwtAuthenticationProvider.authenticate(authentication) }
                        .errorType shouldBe TOKEN_EXPIRED
                }
            }
        },
    )
