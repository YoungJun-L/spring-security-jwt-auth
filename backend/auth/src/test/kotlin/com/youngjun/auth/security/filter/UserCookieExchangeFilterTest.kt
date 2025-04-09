package com.youngjun.auth.security.filter

import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.security.token.JwtAuthenticationToken
import com.youngjun.auth.support.SecurityTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import jakarta.servlet.http.HttpServletRequest
import org.springframework.mock.web.MockFilterChain
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.security.core.context.SecurityContextHolder

@SecurityTest
class UserCookieExchangeFilterTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            afterTest { SecurityContextHolder.clearContext() }

            context("인증 정보를 쿠키로 교환") {
                val request = MockHttpServletRequest()
                val response = MockHttpServletResponse()
                val filterChain = MockFilterChain()

                test("성공") {
                    val account = AccountBuilder().build()
                    SecurityContextHolder.getContext().authentication = JwtAuthenticationToken.authenticated(account)

                    UserCookieExchangeFilter.doFilter(request, response, filterChain)

                    val actual = (filterChain.request as HttpServletRequest).cookies.first { it.name == "USER_ID" }.value
                    actual shouldBe account.id.toString()
                }

                test("익명 유저인 경우") {
                    SecurityContextHolder.getContext().authentication =
                        AnonymousAuthenticationToken("key", "anonymousUser", AuthorityUtils.createAuthorityList("ROLE_ANONYMOUS"))

                    UserCookieExchangeFilter.doFilter(request, response, filterChain)

                    val actual = (filterChain.request as HttpServletRequest).cookies.first { it.name == "USER_ID" }.value
                    actual shouldBe "0"
                }
            }
        },
    )
