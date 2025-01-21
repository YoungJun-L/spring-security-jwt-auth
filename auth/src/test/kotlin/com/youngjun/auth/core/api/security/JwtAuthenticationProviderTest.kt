package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.domain.account.AccountBuilder
import com.youngjun.auth.core.domain.account.AccountReader
import com.youngjun.auth.core.domain.token.JwtBuilder
import com.youngjun.auth.core.domain.token.TokenParser
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.ACCOUNT_DISABLED_ERROR
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.security.authentication.AccountStatusException
import org.springframework.security.authentication.CredentialsExpiredException

class JwtAuthenticationProviderTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val tokenParser = mockk<TokenParser>()
            val accountReader = mockk<AccountReader>()
            val jwtAuthenticationProvider = JwtAuthenticationProvider(tokenParser, accountReader)

            context("JWT 인증") {
                test("성공") {
                    val userId = 1L
                    val authentication = BearerTokenAuthenticationToken(JwtBuilder(subject = userId.toString()).build())
                    every { tokenParser.parseUserId(any()) } returns userId
                    every { accountReader.readEnabled(any()) } returns AccountBuilder(id = userId).build()

                    val actual = jwtAuthenticationProvider.authenticate(authentication)

                    actual.isAuthenticated shouldBe true
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val userId = 1L
                    val authentication = BearerTokenAuthenticationToken(JwtBuilder(subject = userId.toString()).build())
                    every { tokenParser.parseUserId(any()) } returns userId
                    every { accountReader.readEnabled(any()) } throws AuthException(ACCOUNT_DISABLED_ERROR)

                    shouldThrow<AccountStatusException> { jwtAuthenticationProvider.authenticate(authentication) }
                }

                test("토큰이 유효하지 않으면 실패한다.") {
                    val authentication = BearerTokenAuthenticationToken(JwtBuilder().build())
                    every { tokenParser.parseUserId(any()) } throws InvalidTokenException("")

                    shouldThrow<InvalidTokenException> { jwtAuthenticationProvider.authenticate(authentication) }
                }

                test("토큰이 만료되었으면 실패한다.") {
                    val authentication = BearerTokenAuthenticationToken(JwtBuilder().build())
                    every { tokenParser.parseUserId(any()) } throws CredentialsExpiredException("")

                    shouldThrow<CredentialsExpiredException> { jwtAuthenticationProvider.authenticate(authentication) }
                }
            }
        },
    )
