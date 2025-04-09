package com.youngjun.auth.security.filter

import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.security.token.BearerTokenResolver
import com.youngjun.auth.security.token.JwtAuthenticationToken
import com.youngjun.auth.support.SecurityTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import org.springframework.http.HttpHeaders
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationFailureHandler

@SecurityTest
class JwtAuthenticationFilterTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val authenticationManager = mockk<AuthenticationManager>()
            val authenticationFailureHandler = mockk<AuthenticationFailureHandler>()
            val jwtAuthenticationFilter =
                JwtAuthenticationFilter(BearerTokenResolver, authenticationManager, authenticationFailureHandler)

            afterTest { SecurityContextHolder.clearContext() }

            context("JWT 인증 처리") {
                val request = MockHttpServletRequest()
                val response = MockHttpServletResponse()
                val filterChain = MockFilterChain()

                test("성공") {
                    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer a.b.c")
                    every { authenticationManager.authenticate(any()) } returns
                        JwtAuthenticationToken.authenticated(AccountBuilder().build())

                    jwtAuthenticationFilter.doFilter(request, response, filterChain)

                    SecurityContextHolder.getContext().authentication.isAuthenticated shouldBe true
                }

                test("resolve 값이 없는 경우") {
                    jwtAuthenticationFilter.doFilter(request, response, filterChain)

                    SecurityContextHolder.getContext().authentication shouldBe null
                }

                test("인증에 실패하는 경우") {
                    every { authenticationFailureHandler.onAuthenticationFailure(any(), any(), any()) } just Runs

                    jwtAuthenticationFilter.doFilter(request, response, filterChain)

                    SecurityContextHolder.getContext().authentication shouldBe null
                }
            }
        },
    )
