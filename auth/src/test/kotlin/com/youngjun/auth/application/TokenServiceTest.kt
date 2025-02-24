package com.youngjun.auth.application

import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.domain.account.AccountRepository
import com.youngjun.auth.domain.account.AccountStatus
import com.youngjun.auth.domain.support.seconds
import com.youngjun.auth.domain.token.JwtBuilder
import com.youngjun.auth.domain.token.RawAccessToken
import com.youngjun.auth.domain.token.RawRefreshToken
import com.youngjun.auth.domain.token.RefreshTokenBuilder
import com.youngjun.auth.domain.token.RefreshTokenRepository
import com.youngjun.auth.domain.token.TokenStatus
import com.youngjun.auth.infra.jwt.JwtProperties
import com.youngjun.auth.support.ApplicationContextTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DISABLED
import com.youngjun.auth.support.error.ErrorType.TOKEN_EXPIRED
import com.youngjun.auth.support.error.ErrorType.TOKEN_INVALID
import com.youngjun.auth.support.error.ErrorType.TOKEN_NOT_FOUND
import com.youngjun.auth.support.error.ErrorType.UNAUTHORIZED
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import java.time.Duration
import java.time.LocalDateTime

@ApplicationContextTest
class TokenServiceTest(
    private val tokenService: TokenService,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val accountRepository: AccountRepository,
    private val jwtProperties: JwtProperties,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("토큰 발급") {
                test("성공") {
                    val userId = 1L

                    val actual = tokenService.issue(userId)

                    actual.userId shouldBe userId
                    actual.accessToken.userId shouldBe userId
                    actual.refreshToken.userId shouldBe userId
                }
            }

            context("토큰 재발급") {
                test("refreshToken 이 아직 만료되지 않았으면 refreshToken 은 갱신되지 않는다.") {
                    val account = accountRepository.save(AccountBuilder().build())
                    val now = LocalDateTime.now()
                    val rawRefreshToken =
                        RawRefreshToken(
                            JwtBuilder(
                                subject = account.id.toString(),
                                issuedAt = now,
                                expiresIn = jwtProperties.expirationThreshold + 1.seconds,
                                secretKey = jwtProperties.refreshSecretKey,
                            ).build(),
                        )
                    refreshTokenRepository.save(RefreshTokenBuilder(account.id, rawRefreshToken.value).build())

                    val actual = tokenService.reissue(rawRefreshToken, now)

                    actual.userId shouldBe account.id
                    actual.accessToken.userId shouldBe account.id
                    actual.refreshToken.isNotEmpty() shouldBe false
                }

                test("refreshToken 이 곧 만료되면 refreshToken 도 갱신된다.") {
                    val account = accountRepository.save(AccountBuilder().build())
                    val now = LocalDateTime.now()
                    val rawRefreshToken =
                        RawRefreshToken(
                            JwtBuilder(
                                subject = account.id.toString(),
                                issuedAt = now,
                                expiresIn = jwtProperties.expirationThreshold,
                                secretKey = jwtProperties.refreshSecretKey,
                            ).build(),
                        )
                    refreshTokenRepository.save(RefreshTokenBuilder(account.id, rawRefreshToken.value).build())

                    val actual = tokenService.reissue(rawRefreshToken, now)

                    actual.userId shouldBe account.id
                    actual.accessToken.userId shouldBe account.id
                    actual.refreshToken.userId shouldBe account.id
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val account = accountRepository.save(AccountBuilder(status = AccountStatus.DISABLED).build())
                    val rawRefreshToken =
                        RawRefreshToken(JwtBuilder(subject = account.id.toString(), secretKey = jwtProperties.refreshSecretKey).build())
                    refreshTokenRepository.save(RefreshTokenBuilder(account.id, rawRefreshToken.value).build())

                    shouldThrow<AuthException> { tokenService.reissue(rawRefreshToken) }
                        .errorType shouldBe ACCOUNT_DISABLED
                }

                test("refreshToken 이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { tokenService.reissue(RawRefreshToken("INVALID")) }
                        .errorType shouldBe TOKEN_INVALID
                }

                test("refreshToken 의 유효 기간이 지났으면 실패한다.") {
                    val account = accountRepository.save(AccountBuilder().build())
                    val rawRefreshToken =
                        RawRefreshToken(
                            JwtBuilder(
                                subject = account.id.toString(),
                                expiresIn = Duration.ZERO,
                                secretKey = jwtProperties.refreshSecretKey,
                            ).build(),
                        )
                    refreshTokenRepository.save(RefreshTokenBuilder(account.id, rawRefreshToken.value).build())

                    shouldThrow<AuthException> { tokenService.reissue(rawRefreshToken) }
                        .errorType shouldBe TOKEN_EXPIRED
                }

                test("refreshToken 이 만료되었으면 실패한다.") {
                    val account = accountRepository.save(AccountBuilder().build())
                    val rawRefreshToken =
                        RawRefreshToken(JwtBuilder(subject = account.id.toString(), secretKey = jwtProperties.refreshSecretKey).build())
                    refreshTokenRepository.save(RefreshTokenBuilder(account.id, rawRefreshToken.value, TokenStatus.EXPIRED).build())

                    shouldThrow<AuthException> { tokenService.reissue(rawRefreshToken) }
                        .errorType shouldBe TOKEN_EXPIRED
                }

                test("refreshToken 이 존재하지 않으면 실패한다.") {
                    shouldThrow<AuthException> {
                        tokenService.reissue(RawRefreshToken(JwtBuilder(secretKey = jwtProperties.refreshSecretKey).build()))
                    }.errorType shouldBe TOKEN_NOT_FOUND
                }

                test("refreshToken 은 있으나 유저가 존재하지 않으면 실패한다.") {
                    val account = AccountBuilder().build()
                    val rawRefreshToken =
                        RawRefreshToken(JwtBuilder(subject = account.id.toString(), secretKey = jwtProperties.refreshSecretKey).build())
                    refreshTokenRepository.save(RefreshTokenBuilder(account.id, rawRefreshToken.value).build())

                    shouldThrow<AuthException> { tokenService.reissue(rawRefreshToken) }
                        .errorType shouldBe UNAUTHORIZED
                }
            }

            context("accessToken 파싱") {
                test("성공") {
                    val account = accountRepository.save(AccountBuilder().build())
                    val rawAccessToken =
                        RawAccessToken(JwtBuilder(subject = account.id.toString(), secretKey = jwtProperties.accessSecretKey).build())

                    val actual = tokenService.parse(rawAccessToken)

                    actual.id shouldBe account.id
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val account = accountRepository.save(AccountBuilder(status = AccountStatus.DISABLED).build())
                    val rawAccessToken =
                        RawAccessToken(JwtBuilder(subject = account.id.toString(), secretKey = jwtProperties.accessSecretKey).build())

                    shouldThrow<AuthException> { tokenService.parse(rawAccessToken) }
                        .errorType shouldBe ACCOUNT_DISABLED
                }

                test("accessToken 이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { tokenService.parse(RawAccessToken("INVALID")) }
                        .errorType shouldBe TOKEN_INVALID
                }

                test("accessToken 의 유효 기간이 지났으면 실패한다.") {
                    val account = accountRepository.save(AccountBuilder().build())
                    val rawAccessToken =
                        RawAccessToken(
                            JwtBuilder(
                                subject = account.id.toString(),
                                expiresIn = Duration.ZERO,
                                secretKey = jwtProperties.accessSecretKey,
                            ).build(),
                        )
                    refreshTokenRepository.save(RefreshTokenBuilder(account.id, rawAccessToken.value).build())

                    shouldThrow<AuthException> { tokenService.parse(rawAccessToken) }
                        .errorType shouldBe TOKEN_EXPIRED
                }
            }
        },
    )
