package com.youngjun.auth.core.api.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.youngjun.auth.core.api.support.SecurityTest
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder

class UserDetailsExchangeFilterTest(
    private val objectMapper: ObjectMapper,
) : SecurityTest() {
    private lateinit var userDetailsExchangeFilter: UserDetailsExchangeFilter
    private lateinit var request: MockHttpServletRequest
    private lateinit var response: MockHttpServletResponse
    private lateinit var filterChain: MockFilterChain

    @BeforeEach
    fun setUp() {
        userDetailsExchangeFilter = UserDetailsExchangeFilter(objectMapper)
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()
        filterChain = MockFilterChain()
    }

    @Test
    fun `인증 정보를 쿠키로 교환 성공`() {
        val username = "username123"
        val authentication = mockk<Authentication>()
        SecurityContextHolder.getContext().authentication = authentication
        every { authentication.principal } returns username
        every { authentication.details } returns emptyMap<String, Any>()

        userDetailsExchangeFilter.doFilter(request, response, filterChain)

        val request = filterChain.request as HttpServletRequest
        val actual = request.cookies.first { it.name == "user" }.value
        actual shouldBe "{\"username\":\"$username\",\"details\":{}}"
        SecurityContextHolder.getContext().authentication shouldBe null
    }
}
