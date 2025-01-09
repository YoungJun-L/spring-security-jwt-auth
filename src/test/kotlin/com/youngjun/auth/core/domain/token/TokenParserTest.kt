package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import com.youngjun.auth.core.api.support.error.ErrorType.TOKEN_INVALID_ERROR
import com.youngjun.auth.core.domain.support.DomainTest
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Value

@DomainTest
class TokenParserTest(
    private val tokenParser: TokenParser,
    @Value("\${spring.security.jwt.secret-key}") private val secretKey: String,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("subject 파싱") {
                test("성공") {
                    val subject = "username123"
                    val token = JwtBuilder(secretKey = secretKey, subject = subject).build()

                    val actual = tokenParser.parseSubject(token)

                    actual shouldBe subject
                }

                test("만료된 경우 실패한다.") {
                    val token = JwtBuilder(secretKey = secretKey, expiresInSeconds = 0).build()

                    shouldThrow<AuthException> { tokenParser.parseSubject(token) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }

                test("유효하지 않는 경우 실패한다.") {
                    val token = JwtBuilder(secretKey = "a".repeat(100), expiresInSeconds = 0).build()

                    shouldThrow<AuthException> { tokenParser.parseSubject(token) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }
            }

            context("토큰 검증") {
                test("성공") {
                    val token = JwtBuilder(secretKey = secretKey).build()

                    shouldNotThrow<AuthException> { tokenParser.verify(RefreshTokenBuilder(token).build()) }
                }

                test("만료된 경우") {
                    val token = JwtBuilder(secretKey = secretKey, expiresInSeconds = 0).build()

                    shouldThrow<AuthException> { tokenParser.verify(RefreshTokenBuilder(token).build()) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }

                test("유효하지 않는 경우") {
                    val token = JwtBuilder(secretKey = "a".repeat(100), expiresInSeconds = 0).build()

                    shouldThrow<AuthException> { tokenParser.verify(RefreshTokenBuilder(token).build()) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }
            }
        },
    )
