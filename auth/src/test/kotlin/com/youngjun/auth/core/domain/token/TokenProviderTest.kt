package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.api.security.InvalidTokenException
import com.youngjun.auth.core.domain.account.AccountBuilder
import com.youngjun.auth.core.support.DomainTest
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_INVALID_ERROR
import io.jsonwebtoken.Jwts
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import org.springframework.security.authentication.CredentialsExpiredException
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

@DomainTest
class TokenProviderTest :
    FunSpec(
        {
            isolationMode = IsolationMode.InstancePerLeaf

            val secretKey =
                Jwts.SIG.HS256
                    .key()
                    .build()
            val secretKeyHolder = SecretKeyHolder(secretKey)
            val accessExpiresIn: Long = 2.hours.inWholeMilliseconds
            val refreshExpiresIn: Long = 1.days.inWholeMilliseconds
            val now = System.currentTimeMillis()
            val tokenProvider = TokenProvider(secretKeyHolder, { now }, accessExpiresIn, refreshExpiresIn)

            context("subject 파싱") {
                test("성공") {
                    val subject = "username123"
                    val token = JwtBuilder(secretKey = secretKey, subject = subject).build()

                    val actual = tokenProvider.parseSubject(token)

                    actual shouldBe subject
                }

                test("만료된 경우 실패한다.") {
                    val token = JwtBuilder(secretKey = secretKeyHolder.get(), expiresInMilliseconds = 0).build()

                    shouldThrow<CredentialsExpiredException> { tokenProvider.parseSubject(token) }
                }

                test("유효하지 않는 경우 실패한다.") {
                    val token = JwtBuilder().build()

                    shouldThrow<InvalidTokenException> { tokenProvider.parseSubject(token) }
                }
            }

            context("토큰 검증") {
                test("성공") {
                    val token = JwtBuilder(secretKey = secretKeyHolder.get()).build()

                    shouldNotThrow<AuthException> { tokenProvider.verify(RefreshTokenBuilder(token).build()) }
                }

                test("만료된 경우 실패한다.") {
                    val token = JwtBuilder(secretKey = secretKeyHolder.get(), expiresInMilliseconds = 0).build()

                    shouldThrow<AuthException> { tokenProvider.verify(RefreshTokenBuilder(token).build()) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }

                test("유효하지 않는 경우 실패한다.") {
                    val token = JwtBuilder().build()

                    shouldThrow<AuthException> { tokenProvider.verify(RefreshTokenBuilder(token).build()) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }
            }

            context("발급") {
                test("성공") {
                    val account = AccountBuilder().build()

                    val actual = tokenProvider.generate(account)

                    val parser = Jwts.parser().verifyWith(secretKeyHolder.get()).build()
                    parser.parseSignedClaims(actual.accessToken).payload.subject shouldBe account.username
                    parser.parseSignedClaims(actual.refreshToken).payload.subject shouldBe account.username
                }

                test("access token 만료 시간 검증") {
                    val account = AccountBuilder().build()

                    val actual = tokenProvider.generate(account)

                    actual.accessTokenExpiration shouldBe now + accessExpiresIn
                }

                test("refresh token 만료 시간 검증") {
                    val account = AccountBuilder().build()

                    val actual = tokenProvider.generate(account)

                    actual.refreshTokenExpiration shouldBe now + refreshExpiresIn
                }
            }
        },
    )
