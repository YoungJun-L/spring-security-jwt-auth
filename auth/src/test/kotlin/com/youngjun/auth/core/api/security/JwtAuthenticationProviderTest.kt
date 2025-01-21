package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.api.application.AccountService
import com.youngjun.auth.core.domain.account.AccountBuilder
import com.youngjun.auth.core.domain.account.AccountStatus
import com.youngjun.auth.core.domain.token.JwtBuilder
import com.youngjun.auth.core.domain.token.TokenProvider
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

            val tokenProvider = mockk<TokenProvider>()
            val accountService = mockk<AccountService>()
            val jwtAuthenticationProvider = JwtAuthenticationProvider(tokenProvider, accountService)

            context("JWT 인증") {
                test("성공") {
                    val username = "username123"
                    val authentication = BearerTokenAuthenticationToken(JwtBuilder(subject = username).build())
                    every { tokenProvider.parseSubject(any()) } returns username
                    every { accountService.loadUserByUsername(any()) } returns AccountBuilder(username = username).build()

                    val actual = jwtAuthenticationProvider.authenticate(authentication)

                    actual.isAuthenticated shouldBe true
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val username = "username123"
                    val authentication = BearerTokenAuthenticationToken(JwtBuilder(subject = username).build())
                    every { tokenProvider.parseSubject(any()) } returns username
                    every { accountService.loadUserByUsername(any()) } returns
                        AccountBuilder(
                            username = username,
                            status = AccountStatus.DISABLED,
                        ).build()

                    shouldThrow<AccountStatusException> { jwtAuthenticationProvider.authenticate(authentication) }
                }

                test("토큰이 유효하지 않으면 실패한다.") {
                    val authentication = BearerTokenAuthenticationToken(JwtBuilder().build())
                    every { tokenProvider.parseSubject(any()) } throws InvalidTokenException("")

                    shouldThrow<InvalidTokenException> { jwtAuthenticationProvider.authenticate(authentication) }
                }

                test("토큰이 만료되었으면 실패한다.") {
                    val authentication = BearerTokenAuthenticationToken(JwtBuilder().build())
                    every { tokenProvider.parseSubject(any()) } throws CredentialsExpiredException("")

                    shouldThrow<CredentialsExpiredException> { jwtAuthenticationProvider.authenticate(authentication) }
                }
            }
        },
    )
