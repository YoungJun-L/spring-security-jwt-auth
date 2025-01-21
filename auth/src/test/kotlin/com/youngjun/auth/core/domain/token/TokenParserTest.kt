package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.DomainTest
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_INVALID_ERROR
import io.jsonwebtoken.Jwts
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

@DomainTest
class TokenParserTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val secretKey =
                Jwts.SIG.HS256
                    .key()
                    .build()
            val secretKeyHolder = SecretKeyHolder(secretKey)
            val tokenParser = TokenParser(secretKeyHolder)

            context("userId 파싱") {
                test("성공") {
                    val userId = 1L
                    val token = JwtBuilder(secretKey = secretKey, subject = userId.toString()).build()

                    val actual = tokenParser.parseUserId(token)

                    actual shouldBe userId
                }

                test("만료된 경우 실패한다.") {
                    val token = JwtBuilder(secretKey = secretKeyHolder.get(), expiresInMilliseconds = 0).build()

                    shouldThrow<AuthException> { tokenParser.parseUserId(token) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }

                test("유효하지 않는 경우 실패한다.") {
                    val token = JwtBuilder().build()

                    shouldThrow<AuthException> { tokenParser.parseUserId(token) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }
            }
        },
    )
