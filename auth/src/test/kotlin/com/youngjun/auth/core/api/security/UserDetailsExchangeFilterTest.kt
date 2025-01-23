package com.youngjun.auth.core.api.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.youngjun.auth.core.domain.account.AccountBuilder
import com.youngjun.auth.core.support.SecurityTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import jakarta.servlet.http.HttpServletRequest
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder

@SecurityTest
class UserDetailsExchangeFilterTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val objectMapper = jacksonObjectMapper()
            val userDetailsExchangeFilter = UserDetailsExchangeFilter(objectMapper)

            afterTest { SecurityContextHolder.clearContext() }

            context("인증 정보를 쿠키로 교환") {
                val request = MockHttpServletRequest()
                val response = MockHttpServletResponse()
                val filterChain = MockFilterChain()

                test("성공") {
                    val authentication = JwtAuthenticationToken.authenticated(AccountBuilder().build())
                    SecurityContextHolder.getContext().authentication = authentication

                    userDetailsExchangeFilter.doFilter(request, response, filterChain)

                    val actual = (filterChain.request as HttpServletRequest).cookies.first { it.name == "user" }.value
                    val expect =
                        objectMapper.writeValueAsString(
                            mapOf(
                                "id" to authentication.principal,
                                "details" to authentication.details,
                            ),
                        )
                    actual shouldBe expect
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
