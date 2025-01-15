package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.domain.account.AccountBuilder
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationFailureHandler

class JwtAuthenticationFilterTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val bearerTokenResolver = mockk<BearerTokenResolver>()
            val authenticationManager = mockk<AuthenticationManager>()
            val authenticationFailureHandler = mockk<AuthenticationFailureHandler>()
            val jwtAuthenticationFilter =
                JwtAuthenticationFilter(bearerTokenResolver, authenticationManager, authenticationFailureHandler)

            afterTest { SecurityContextHolder.clearContext() }

            context("JWT 인증 처리") {
                val request = MockHttpServletRequest()
                val response = MockHttpServletResponse()
                val filterChain = MockFilterChain()

                test("성공") {
                    every { bearerTokenResolver.resolve(any()) } returns "token"
                    every { authenticationManager.authenticate(any()) } returns
                        JwtAuthenticationToken.authenticated(AccountBuilder().build())

                    jwtAuthenticationFilter.doFilter(request, response, filterChain)

                    SecurityContextHolder.getContext().authentication.isAuthenticated shouldBe true
                }

                test("resolve 값이 없는 경우") {
                    every { bearerTokenResolver.resolve(any()) } returns ""

                    jwtAuthenticationFilter.doFilter(request, response, filterChain)

                    SecurityContextHolder.getContext().authentication shouldBe null
                }

                test("인증에 실패하는 경우") {
                    every { bearerTokenResolver.resolve(any()) } throws BadCredentialsException("")
                    every { authenticationFailureHandler.onAuthenticationFailure(any(), any(), any()) } just Runs

                    jwtAuthenticationFilter.doFilter(request, response, filterChain)

                    SecurityContextHolder.getContext().authentication shouldBe null
                }
            }
        },
    )
