package com.youngjun.auth.security.filter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.youngjun.auth.api.controller.v1.response.LogoutResponse
import com.youngjun.auth.application.AccountService
import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.security.handler.JsonResponseWriter
import com.youngjun.auth.security.token.JwtAuthenticationToken
import com.youngjun.auth.support.SecurityTest
import com.youngjun.auth.support.response.AuthResponse
import com.youngjun.auth.support.response.ResultType
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder

@SecurityTest
class LogoutFilterTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val accountService = mockk<AccountService>()
            val objectMapper = jacksonObjectMapper()
            val logoutFilter = LogoutFilter(accountService, JsonResponseWriter(objectMapper))

            afterTest { SecurityContextHolder.clearContext() }

            context("로그아웃") {
                val request =
                    MockHttpServletRequest().apply {
                        method = HttpMethod.POST.name()
                        servletPath = "/auth/logout"
                    }
                val response = MockHttpServletResponse()
                val filterChain = MockFilterChain()

                test("성공") {
                    val account = AccountBuilder().build()
                    SecurityContextHolder.getContext().authentication = JwtAuthenticationToken.authenticated(account)
                    every { accountService.logout(any()) } returns account.logout()

                    logoutFilter.doFilter(request, response, filterChain)

                    response.status shouldBe HttpStatus.OK.value()
                    objectMapper.readValue<AuthResponse<LogoutResponse>>(response.contentAsByteArray).status shouldBe ResultType.SUCCESS
                }

                test("성공하면 인증 정보는 비워진다.") {
                    val account = AccountBuilder().build()
                    SecurityContextHolder.getContext().authentication = JwtAuthenticationToken.authenticated(account)
                    every { accountService.logout(any()) } returns account.logout()

                    logoutFilter.doFilter(request, response, filterChain)

                    SecurityContextHolder.getContext().authentication shouldBe null
                }
            }
        },
    )
