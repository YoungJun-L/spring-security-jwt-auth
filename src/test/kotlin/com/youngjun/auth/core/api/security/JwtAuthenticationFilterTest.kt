package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.api.support.SecurityTest
import com.youngjun.auth.core.domain.token.TokenParser
import com.youngjun.auth.core.domain.user.UserBuilder
import com.youngjun.auth.core.domain.user.UserReader
import io.kotest.matchers.shouldBe
import io.mockk.every
import jakarta.servlet.FilterChain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationFailureHandler

class JwtAuthenticationFilterTest(
    private val authenticationManager: AuthenticationManager,
    private val authenticationFailureHandler: AuthenticationFailureHandler,
    private val tokenParser: TokenParser,
    private val userReader: UserReader,
) : SecurityTest() {
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter
    private lateinit var request: MockHttpServletRequest
    private lateinit var response: MockHttpServletResponse
    private lateinit var filterChain: FilterChain

    @BeforeEach
    fun setUp() {
        jwtAuthenticationFilter =
            JwtAuthenticationFilter(
                BearerTokenResolver(),
                authenticationManager,
                authenticationFailureHandler,
            )
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()
        filterChain = MockFilterChain()
    }

    @Test
    fun `JWT 인증 처리 성공`() {
        val value = "a.b.c"
        val username = "username123"
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer $value")
        every { tokenParser.parseSubject(value) } returns username
        every { userReader.read(username) } returns UserBuilder(username = username).build()

        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        SecurityContextHolder.getContext().authentication.isAuthenticated shouldBe true
    }

    @Test
    fun `헤더에 값이 없을 때 처리되지 않는다`() {
        request.addHeader(HttpHeaders.AUTHORIZATION, "")

        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        SecurityContextHolder.getContext().authentication shouldBe null
    }

    @Test
    fun `인증에 실패하면 실패 핸들러가 실행 된다`() {
        val value = "a.b.c"
        request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer $value")
        every { tokenParser.parseSubject(value) } throws BadCredentialsException("")

        jwtAuthenticationFilter.doFilter(request, response, filterChain)

        SecurityContextHolder.getContext().authentication shouldBe null
        response.status shouldBe HttpStatus.UNAUTHORIZED.value()
    }
}
