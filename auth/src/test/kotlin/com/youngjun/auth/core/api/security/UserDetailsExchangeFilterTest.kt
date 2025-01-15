package com.youngjun.auth.core.api.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.youngjun.auth.core.domain.account.AccountBuilder
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import jakarta.servlet.http.HttpServletRequest
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder

class UserDetailsExchangeFilterTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val userDetailsExchangeFilter = UserDetailsExchangeFilter(jacksonObjectMapper())

            afterTest { SecurityContextHolder.clearContext() }

            context("인증 정보를 쿠키로 교환") {
                val request = MockHttpServletRequest()
                val response = MockHttpServletResponse()
                val filterChain = MockFilterChain()

                test("성공") {
                    val username = "username123"
                    SecurityContextHolder.getContext().authentication =
                        JwtAuthenticationToken.authenticated(AccountBuilder(username = username).build())

                    userDetailsExchangeFilter.doFilter(request, response, filterChain)

                    val actual = (filterChain.request as HttpServletRequest).cookies.first { it.name == "user" }.value
                    actual shouldBe "{\"username\":\"$username\",\"details\":{}}"
                }

                test("성공하면 인증 정보는 비워진다.") {
                    SecurityContextHolder.getContext().authentication =
                        JwtAuthenticationToken.authenticated(AccountBuilder().build())

                    userDetailsExchangeFilter.doFilter(request, response, filterChain)

                    SecurityContextHolder.getContext().authentication shouldBe null
                }
            }
        },
    )
