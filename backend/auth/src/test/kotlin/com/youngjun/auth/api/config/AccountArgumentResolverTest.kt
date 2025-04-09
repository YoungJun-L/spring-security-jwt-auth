package com.youngjun.auth.api.config

import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.security.token.JwtAuthenticationToken
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.DEFAULT
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.core.MethodParameter
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.context.request.ServletWebRequest
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.javaMethod

class AccountArgumentResolverTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            afterTest { SecurityContextHolder.clearContext() }

            class TestController {
                fun account(account: Account) {}

                fun notAccount() {}
            }

            context("파라미터 지원 여부 확인") {
                test("성공") {
                    val method = TestController::class.declaredFunctions.first { it.name == "account" }.javaMethod!!
                    val methodParameter = MethodParameter.forExecutable(method, 0)

                    AccountArgumentResolver.supportsParameter(methodParameter) shouldBe true
                }

                test("파라미터가 없는 경우") {
                    val method = TestController::class.declaredFunctions.first { it.name == "notAccount" }.javaMethod!!
                    val methodParameter = MethodParameter.forExecutable(method, -1)

                    AccountArgumentResolver.supportsParameter(methodParameter) shouldBe false
                }
            }

            context("계정 정보 추출") {
                val method = TestController::class.declaredFunctions.first { it.name == "account" }.javaMethod!!
                val methodParameter = MethodParameter.forExecutable(method, 0)
                val webRequest = ServletWebRequest(MockHttpServletRequest())

                test("성공") {
                    val account = AccountBuilder().build()
                    SecurityContextHolder.getContext().authentication = JwtAuthenticationToken.authenticated(account)

                    val actual =
                        AccountArgumentResolver.resolveArgument(
                            parameter = methodParameter,
                            webRequest = webRequest,
                            mavContainer = null,
                            binderFactory = null,
                        )

                    actual.id shouldBe account.id
                }

                test("인증 정보가 없으면 실패한다.") {
                    SecurityContextHolder.getContext().authentication = null

                    shouldThrow<AuthException> {
                        AccountArgumentResolver.resolveArgument(
                            parameter = methodParameter,
                            webRequest = webRequest,
                            mavContainer = null,
                            binderFactory = null,
                        )
                    }.errorType shouldBe DEFAULT
                }

                test("성공하면 인증 정보는 비워진다.") {
                    SecurityContextHolder.getContext().authentication = JwtAuthenticationToken.authenticated(AccountBuilder().build())

                    AccountArgumentResolver.resolveArgument(
                        parameter = methodParameter,
                        webRequest = webRequest,
                        mavContainer = null,
                        binderFactory = null,
                    )

                    SecurityContextHolder.getContext().authentication shouldBe null
                }
            }
        },
    )
