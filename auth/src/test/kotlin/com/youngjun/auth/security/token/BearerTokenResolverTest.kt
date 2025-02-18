package com.youngjun.auth.security.token

import com.youngjun.auth.security.support.error.TypedAuthenticationException
import com.youngjun.auth.support.SecurityTest
import com.youngjun.auth.support.error.ErrorType.TOKEN_INVALID
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.mock.web.MockHttpServletRequest

@SecurityTest
class BearerTokenResolverTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val request = MockHttpServletRequest()

            context("토큰 추출") {
                test("성공") {
                    request.addHeader(AUTHORIZATION, "Bearer a.b.c")

                    val actual = BearerTokenResolver.resolve(request)

                    actual shouldBe "a.b.c"
                }

                test("Bearer 로 시작하지 않으면 빈 값을 반환한다.") {
                    request.addHeader(AUTHORIZATION, "No-Bearer a.b.c")

                    val actual = BearerTokenResolver.resolve(request)

                    actual shouldBe ""
                }
            }

            context("토큰의 형식이 다르면 추출에 실패한다.") {
                arrayOf(" . . ", "a.b", " ").forEach {
                    test("\"$it\"") {
                        request.addHeader(AUTHORIZATION, "Bearer $it")

                        shouldThrow<TypedAuthenticationException> { BearerTokenResolver.resolve(request) }
                            .errorType shouldBe TOKEN_INVALID
                    }
                }
            }
        },
    )
