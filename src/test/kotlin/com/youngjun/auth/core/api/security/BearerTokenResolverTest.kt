package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.domain.support.DomainTest
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders.AUTHORIZATION

@DomainTest
class BearerTokenResolverTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val request = mockk<HttpServletRequest>()
            val bearerTokenResolver = BearerTokenResolver()

            context("토큰 추출") {
                test("성공") {
                    val value = "Bearer a.b.c"
                    every { request.getHeader(AUTHORIZATION) } returns value

                    val actual = bearerTokenResolver.resolve(request)
                    actual shouldBe "a.b.c"
                }

                test("Bearer 로 시작하지 않는 경우") {
                    val value = "a.b.c"
                    every { request.getHeader(AUTHORIZATION) } returns value

                    val actual = bearerTokenResolver.resolve(request)
                    actual shouldBe null
                }
            }

            context("유효하지 않은 토큰으로 추출") {
                arrayOf(" . . ", "a.b", " ").forEach {
                    test("\"$it\"") {
                        val value = "Bearer $it"
                        every { request.getHeader(AUTHORIZATION) } returns value

                        shouldThrow<Exception> { bearerTokenResolver.resolve(request) }
                    }
                }
            }
        },
    )
