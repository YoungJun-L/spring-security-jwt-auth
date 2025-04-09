package com.youngjun.auth.security.filter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.youngjun.auth.domain.account.EMAIL_ADDRESS
import com.youngjun.auth.domain.account.NoOperationPasswordEncoder
import com.youngjun.auth.domain.account.Password
import com.youngjun.auth.domain.account.RAW_PASSWORD
import com.youngjun.auth.security.support.error.TypedAuthenticationException
import com.youngjun.auth.support.SecurityTest
import com.youngjun.auth.support.error.ErrorType.BAD_REQUEST
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler

@SecurityTest
class JsonLoginAuthenticationFilterTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val authenticationManager = mockk<AuthenticationManager>()
            val objectMapper = jacksonObjectMapper()
            val authenticationSuccessHandler = mockk<AuthenticationSuccessHandler>()
            val authenticationFailureHandler = mockk<AuthenticationFailureHandler>()
            val jsonLoginAuthenticationFilter =
                JsonLoginAuthenticationFilter(
                    authenticationManager,
                    objectMapper,
                    authenticationSuccessHandler,
                    authenticationFailureHandler,
                )

            context("추출 및 인증") {
                val request = MockHttpServletRequest()
                val response = MockHttpServletResponse()

                test("성공") {
                    request.contentType = MediaType.APPLICATION_JSON_VALUE
                    request.setContent(objectMapper.writeValueAsBytes(mapOf("email" to EMAIL_ADDRESS, "password" to RAW_PASSWORD)))
                    every { authenticationManager.authenticate(any()) } returns
                        UsernamePasswordAuthenticationToken.authenticated(
                            EMAIL_ADDRESS.value,
                            Password.encodedWith(RAW_PASSWORD, NoOperationPasswordEncoder).value,
                            AuthorityUtils.NO_AUTHORITIES,
                        )

                    val actual = jsonLoginAuthenticationFilter.attemptAuthentication(request, response)

                    actual.isAuthenticated shouldBe true
                }

                test("정보가 없으면 추출에 실패한다.") {
                    request.contentType = MediaType.APPLICATION_JSON_VALUE
                    request.setContent("".toByteArray())

                    shouldThrow<TypedAuthenticationException> {
                        jsonLoginAuthenticationFilter.attemptAuthentication(request, response)
                    }.errorType shouldBe BAD_REQUEST
                }
            }
        },
    )
