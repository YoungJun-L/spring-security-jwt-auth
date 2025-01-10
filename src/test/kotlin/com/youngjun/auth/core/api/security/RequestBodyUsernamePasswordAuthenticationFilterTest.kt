package com.youngjun.auth.core.api.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.youngjun.auth.core.api.application.TokenService
import com.youngjun.auth.core.api.support.SecurityTest
import com.youngjun.auth.core.domain.user.UserBuilder
import com.youngjun.auth.core.domain.user.UserReader
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.web.authentication.AuthenticationFailureHandler

class RequestBodyUsernamePasswordAuthenticationFilterTest(
    private val authenticationManager: AuthenticationManager,
    private val authenticationFailureHandler: AuthenticationFailureHandler,
    private val objectMapper: ObjectMapper,
    private val tokenService: TokenService,
    private val userReader: UserReader,
) : SecurityTest() {
    private lateinit var requestBodyUsernamePasswordAuthenticationFilter: RequestBodyUsernamePasswordAuthenticationFilter
    private lateinit var request: MockHttpServletRequest
    private lateinit var response: MockHttpServletResponse

    @BeforeEach
    fun setUp() {
        requestBodyUsernamePasswordAuthenticationFilter =
            RequestBodyUsernamePasswordAuthenticationFilter(
                authenticationManager,
                objectMapper,
                IssueJwtAuthenticationSuccessHandler(tokenService, objectMapper),
                authenticationFailureHandler,
            )
        request = MockHttpServletRequest()
        response = MockHttpServletResponse()
    }

    @Test
    fun `로그인 정보 추출 성공`() {
        val username = "username123"
        request.contentType = MediaType.APPLICATION_JSON_VALUE
        request.setContent("{\"username\":\"$username\",\"password\":\"password123!\"}".toByteArray())
        every { userReader.read(username) } returns UserBuilder(username = username).build()

        val actual = requestBodyUsernamePasswordAuthenticationFilter.attemptAuthentication(request, response)

        actual.isAuthenticated shouldBe true
    }

    @Test
    fun `정보가 없으면 추출에 실패한다`() {
        request.contentType = MediaType.APPLICATION_JSON_VALUE
        request.setContent("".toByteArray())

        shouldThrow<BadCredentialsException> {
            requestBodyUsernamePasswordAuthenticationFilter.attemptAuthentication(
                request,
                response,
            )
        }
    }
}
