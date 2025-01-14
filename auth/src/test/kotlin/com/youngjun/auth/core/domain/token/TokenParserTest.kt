package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.api.security.InvalidTokenException
import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import com.youngjun.auth.core.api.support.error.ErrorType.TOKEN_INVALID_ERROR
import com.youngjun.auth.core.domain.support.DomainTest
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.security.authentication.CredentialsExpiredException

@DomainTest
class TokenParserTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val secretKey = "012345678abcdefghijklmnopqrstuvwxyz"
            val tokenParser = TokenParser(secretKey)

            context("subject 파싱") {
                test("성공") {
                    val subject = "username123"
                    val token = JwtBuilder(secretKey = secretKey, subject = subject).build()

                    val actual = tokenParser.parseSubject(token)

                    actual shouldBe subject
                }

                test("만료된 경우 실패한다.") {
                    val token = JwtBuilder(secretKey = secretKey, expiresInSeconds = 0).build()

                    shouldThrow<CredentialsExpiredException> { tokenParser.parseSubject(token) }
                }

                test("유효하지 않는 경우 실패한다.") {
                    val token = JwtBuilder(secretKey = "Invalid $secretKey", expiresInSeconds = 0).build()

                    shouldThrow<InvalidTokenException> { tokenParser.parseSubject(token) }
                }
            }

            context("토큰 검증") {
                test("성공") {
                    val token = JwtBuilder(secretKey = secretKey).build()

                    shouldNotThrow<AuthException> { tokenParser.verify(RefreshTokenBuilder(token).build()) }
                }

                test("만료된 경우 실패한다.") {
                    val token = JwtBuilder(secretKey = secretKey, expiresInSeconds = 0).build()

                    shouldThrow<AuthException> { tokenParser.verify(RefreshTokenBuilder(token).build()) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }

                test("유효하지 않는 경우 실패한다.") {
                    val token = JwtBuilder(secretKey = "Invalid $secretKey", expiresInSeconds = 0).build()

                    shouldThrow<AuthException> { tokenParser.verify(RefreshTokenBuilder(token).build()) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }
            }
        },
    )
