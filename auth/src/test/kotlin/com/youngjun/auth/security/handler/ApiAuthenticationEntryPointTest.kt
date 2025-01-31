package com.youngjun.auth.security.handler

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.youngjun.auth.support.SecurityTest
import com.youngjun.auth.support.response.AuthResponse
import com.youngjun.auth.support.response.ResultType
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.BadCredentialsException

@SecurityTest
class ApiAuthenticationEntryPointTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val objectMapper = jacksonObjectMapper()
            val apiAuthenticationEntryPoint = ApiAuthenticationEntryPoint(JsonResponseWriter(objectMapper))

            context("예외 처리 응답") {
                val request = MockHttpServletRequest()
                val response = MockHttpServletResponse()

                test("성공") {
                    apiAuthenticationEntryPoint.commence(request, response, BadCredentialsException(""))

                    response.status shouldBe HttpStatus.UNAUTHORIZED.value()
                    objectMapper.readValue<AuthResponse<Any>>(response.contentAsByteArray).status shouldBe ResultType.ERROR
                }
            }
        },
    )
