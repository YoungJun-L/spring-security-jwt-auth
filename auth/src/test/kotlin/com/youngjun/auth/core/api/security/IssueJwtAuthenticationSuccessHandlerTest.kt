package com.youngjun.auth.core.api.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.youngjun.auth.core.api.application.TokenService
import com.youngjun.auth.core.api.controller.v1.response.LoginResponse
import com.youngjun.auth.core.api.support.response.AuthResponse
import com.youngjun.auth.core.api.support.response.ResultType
import com.youngjun.auth.core.domain.token.TokenPairBuilder
import com.youngjun.auth.core.domain.user.UserBuilder
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.http.HttpStatus
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils

class IssueJwtAuthenticationSuccessHandlerTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val tokenService = mockk<TokenService>()
            val objectMapper = jacksonObjectMapper()
            val issueJwtAuthenticationSuccessHandler = IssueJwtAuthenticationSuccessHandler(tokenService, objectMapper)

            context("로그인 응답") {
                val request = MockHttpServletRequest()
                val response = MockHttpServletResponse()

                test("성공") {
                    val authentication =
                        UsernamePasswordAuthenticationToken(UserBuilder().build(), null, AuthorityUtils.NO_AUTHORITIES)
                    every { tokenService.issue(any()) } returns TokenPairBuilder().build()

                    issueJwtAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, authentication)

                    response.status shouldBe HttpStatus.OK.value()
                    objectMapper.readValue<AuthResponse<LoginResponse>>(response.contentAsByteArray).status shouldBe ResultType.SUCCESS
                }
            }
        },
    )
