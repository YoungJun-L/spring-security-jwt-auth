package com.youngjun.auth.core.api.security

import com.youngjun.auth.core.domain.token.JwtBuilder
import com.youngjun.auth.core.domain.token.TokenParser
import com.youngjun.auth.core.domain.user.UserBuilder
import com.youngjun.auth.core.domain.user.UserReader
import com.youngjun.auth.core.domain.user.UserStatus
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.security.authentication.AccountStatusException

class JwtAuthenticationProviderTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val tokenParser = mockk<TokenParser>()
            val userReader = mockk<UserReader>()
            val jwtAuthenticationProvider = JwtAuthenticationProvider(tokenParser, userReader)

            context("JWT 인증") {
                test("성공") {
                    val username = "username123"
                    val authentication = BearerTokenAuthenticationToken(JwtBuilder(subject = username).build())
                    every { tokenParser.parseSubject(any()) } returns username
                    every { userReader.read(any()) } returns UserBuilder(username = username).build()

                    val actual = jwtAuthenticationProvider.authenticate(authentication)

                    actual.isAuthenticated shouldBe true
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val username = "username123"
                    val authentication = BearerTokenAuthenticationToken(JwtBuilder(subject = username).build())
                    every { tokenParser.parseSubject(any()) } returns username
                    every { userReader.read(any()) } returns
                        UserBuilder(
                            username = username,
                            status = UserStatus.DISABLED,
                        ).build()

                    shouldThrow<AccountStatusException> { jwtAuthenticationProvider.authenticate(authentication) }
                }
            }
        },
    )
