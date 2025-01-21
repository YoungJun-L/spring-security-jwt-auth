package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.support.error.ErrorType.TOKEN_INVALID_ERROR
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.mock.web.MockHttpServletRequest

class BearerTokenResolverTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val request = MockHttpServletRequest()
            val bearerTokenResolver = BearerTokenResolver()

            context("토큰 추출") {
                test("성공") {
                    request.addHeader(AUTHORIZATION, "Bearer a.b.c")

                    val actual = bearerTokenResolver.resolve(request)
                    actual shouldBe "a.b.c"
                }

                test("Bearer 로 시작하지 않는 경우 빈 값을 반환한다.") {
                    request.addHeader(AUTHORIZATION, "No-Bearer a.b.c")

                    val actual = bearerTokenResolver.resolve(request)
                    actual shouldBe ""
                }
            }

            context("토큰의 형식이 다를 경우 추출에 실패한다.") {
                arrayOf(" . . ", "a.b", " ").forEach {
                    test("\"$it\"") {
                        request.addHeader(AUTHORIZATION, "Bearer $it")

                        shouldThrow<TypedAuthenticationException> { bearerTokenResolver.resolve(request) }
                            .errorType shouldBe TOKEN_INVALID_ERROR
                    }
                }
            }
        },
    )
