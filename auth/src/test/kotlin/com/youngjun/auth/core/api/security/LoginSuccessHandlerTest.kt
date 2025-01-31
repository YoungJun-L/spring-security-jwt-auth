package com.youngjun.auth.core.api.security

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.youngjun.auth.core.api.application.AccountService
import com.youngjun.auth.core.api.application.TokenService
import com.youngjun.auth.core.api.controller.v1.response.LoginResponse
import com.youngjun.auth.core.domain.account.AccountBuilder
import com.youngjun.auth.core.domain.token.TokenPairBuilder
import com.youngjun.auth.core.support.SecurityTest
import com.youngjun.auth.core.support.response.AuthResponse
import com.youngjun.auth.core.support.response.ResultType
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
import org.springframework.security.core.context.SecurityContextHolder

@SecurityTest
class LoginSuccessHandlerTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val accountService = mockk<AccountService>()
            val tokenService = mockk<TokenService>()
            val objectMapper = jacksonObjectMapper()
            val loginSuccessHandler = LoginSuccessHandler(accountService, tokenService, objectMapper)

            afterTest { SecurityContextHolder.clearContext() }

            context("로그인") {
                val request = MockHttpServletRequest()
                val response = MockHttpServletResponse()

                test("성공") {
                    val account = AccountBuilder().build()
                    val authentication =
                        UsernamePasswordAuthenticationToken(
                            account,
                            null,
                            AuthorityUtils.NO_AUTHORITIES,
                        )
                    every { accountService.login(any()) } returns account.enabled()
                    every { tokenService.issue(any()) } returns TokenPairBuilder(userId = account.id).build()

                    loginSuccessHandler.onAuthenticationSuccess(request, response, authentication)

                    response.status shouldBe HttpStatus.OK.value()
                    objectMapper.readValue<AuthResponse<LoginResponse>>(response.contentAsByteArray).status shouldBe ResultType.SUCCESS
                }
                test("성공하면 인증 정보는 비워진다.") {
                    val account = AccountBuilder().build()
                    val authentication =
                        UsernamePasswordAuthenticationToken(
                            account,
                            null,
                            AuthorityUtils.NO_AUTHORITIES,
                        )
                    every { accountService.login(any()) } returns account.enabled()
                    every { tokenService.issue(any()) } returns TokenPairBuilder(userId = account.id).build()

                    loginSuccessHandler.onAuthenticationSuccess(request, response, authentication)

                    SecurityContextHolder.getContext().authentication shouldBe null
                }
            }
        },
    )
