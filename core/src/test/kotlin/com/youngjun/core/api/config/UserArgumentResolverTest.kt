package com.youngjun.core.api.config

import com.youngjun.core.domain.User
import com.youngjun.core.support.error.CoreException
import com.youngjun.core.support.error.ErrorType.DEFAULT
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import jakarta.servlet.http.Cookie
import org.springframework.core.MethodParameter
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.web.context.request.ServletWebRequest
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.jvm.javaMethod

class UserArgumentResolverTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            class TestController {
                fun user(user: User) {}

                fun notUser() {}
            }

            context("파라미터 지원 여부 확인") {
                test("성공") {
                    val method = TestController::class.declaredFunctions.first { it.name == "user" }.javaMethod!!
                    val methodParameter = MethodParameter.forExecutable(method, 0)

                    UserArgumentResolver.supportsParameter(methodParameter) shouldBe true
                }

                test("파라미터가 없는 경우") {
                    val method = TestController::class.declaredFunctions.first { it.name == "notUser" }.javaMethod!!
                    val methodParameter = MethodParameter.forExecutable(method, -1)

                    UserArgumentResolver.supportsParameter(methodParameter) shouldBe false
                }
            }

            context("유저 정보 추출") {
                val method = TestController::class.declaredFunctions.first { it.name == "user" }.javaMethod!!
                val methodParameter = MethodParameter.forExecutable(method, 0)

                test("성공") {
                    val userId = 1L
                    val request = MockHttpServletRequest()
                    request.setCookies(Cookie("USER_ID", userId.toString()))
                    val webRequest = ServletWebRequest(request)

                    val actual =
                        UserArgumentResolver.resolveArgument(
                            parameter = methodParameter,
                            webRequest = webRequest,
                            mavContainer = null,
                            binderFactory = null,
                        )

                    actual.id shouldBe userId
                }

                test("값이 변조되었으면 실패한다.") {
                    val request = MockHttpServletRequest()
                    request.setCookies(Cookie("USER_ID", ""))
                    val webRequest = ServletWebRequest(request)

                    shouldThrow<CoreException> {
                        UserArgumentResolver.resolveArgument(
                            parameter = methodParameter,
                            webRequest = webRequest,
                            mavContainer = null,
                            binderFactory = null,
                        )
                    }.errorType shouldBe DEFAULT
                }
            }
        },
    )
