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
import org.springframework.mock.web.MockHttpServletResponse

@SecurityTest
class JsonResponseWriterTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val objectMapper = jacksonObjectMapper()
            val jsonResponseWriter = JsonResponseWriter(objectMapper)

            context("응답") {
                val response = MockHttpServletResponse()

                test("성공") {
                    jsonResponseWriter.write(response, HttpStatus.OK, AuthResponse.success())

                    response.status shouldBe HttpStatus.OK.value()
                    objectMapper.readValue<AuthResponse<Any>>(response.contentAsByteArray).status shouldBe ResultType.SUCCESS
                }
            }
        },
    )
