package com.youngjun.auth.core.api.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

class RequestBodyUsernamePasswordAuthenticationFilterTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val authenticationManager = mockk<AuthenticationManager>()
            val authenticationSuccessHandler = mockk<AuthenticationSuccessHandler>()
            val authenticationFailureHandler = mockk<AuthenticationFailureHandler>()
            val requestBodyUsernamePasswordAuthenticationFilter =
                RequestBodyUsernamePasswordAuthenticationFilter(
                    authenticationManager,
                    jacksonObjectMapper(),
                    authenticationSuccessHandler,
                    authenticationFailureHandler,
                )

            context("로그인 정보 추출") {
                val request = MockHttpServletRequest()
                val response = MockHttpServletResponse()

                test("성공") {
                    val username = "username123"
                    val password = "password123!"
                    request.contentType = MediaType.APPLICATION_JSON_VALUE
                    request.setContent("{\"username\":\"$username\",\"password\":\"$password\"}".toByteArray())
                    every { authenticationManager.authenticate(any()) } returns
                        UsernamePasswordAuthenticationToken.authenticated(
                            username,
                            password,
                            AuthorityUtils.NO_AUTHORITIES,
                        )

                    val actual =
                        requestBodyUsernamePasswordAuthenticationFilter.attemptAuthentication(request, response)

                    actual.isAuthenticated shouldBe true
                }

                test("정보가 없으면 추출에 실패한다.") {
                    request.contentType = MediaType.APPLICATION_JSON_VALUE
                    request.setContent("".toByteArray())

                    shouldThrow<BadCredentialsException> {
                        requestBodyUsernamePasswordAuthenticationFilter.attemptAuthentication(request, response)
                    }
                }
            }
        },
    )
