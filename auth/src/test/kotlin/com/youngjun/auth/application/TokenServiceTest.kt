package com.youngjun.auth.application

import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.domain.account.AccountStatus
import com.youngjun.auth.domain.support.hours
import com.youngjun.auth.domain.support.seconds
import com.youngjun.auth.domain.token.JwtBuilder
import com.youngjun.auth.domain.token.RawAccessToken
import com.youngjun.auth.domain.token.RawRefreshToken
import com.youngjun.auth.domain.token.RefreshTokenBuilder
import com.youngjun.auth.domain.token.TokenStatus
import com.youngjun.auth.infra.db.AccountJpaRepository
import com.youngjun.auth.infra.db.RefreshTokenJpaRepository
import com.youngjun.auth.security.config.JwtProperties
import com.youngjun.auth.support.ApplicationTest
import com.youngjun.auth.support.error.AuthException
import com.youngjun.auth.support.error.ErrorType.ACCOUNT_DISABLED_ERROR
import com.youngjun.auth.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import com.youngjun.auth.support.error.ErrorType.TOKEN_INVALID_ERROR
import com.youngjun.auth.support.error.ErrorType.TOKEN_NOT_FOUND_ERROR
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import java.time.Duration
import java.time.LocalDateTime

@ApplicationTest
class TokenServiceTest(
    private val tokenService: TokenService,
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
    private val accountJpaRepository: AccountJpaRepository,
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
                }

                test("이전 refresh token 은 교체된다.") {
                    val userId = 1L
                    val refreshToken = refreshTokenJpaRepository.save(RefreshTokenBuilder(userId).build())

                    val actual = tokenService.issue(userId)

                    actual.refreshToken.value shouldNotBe refreshToken.value
                }
            }

            context("토큰 재발급") {
                test("성공") {
                    val account = accountJpaRepository.save(AccountBuilder().build())
                    val rawRefreshToken = RawRefreshToken(JwtBuilder(secretKey = jwtProperties.refreshSecretKey).build())
                    refreshTokenJpaRepository.save(RefreshTokenBuilder(account.id, rawRefreshToken.value).build())

                    val actual = tokenService.reissue(rawRefreshToken)

                    actual.userId shouldBe account.id
                }

                test("refreshToken 이 아직 만료되지 않았으면 refresh token 은 갱신되지 않는다.") {
                    val account = accountJpaRepository.save(AccountBuilder().build())
                    val rawRefreshToken =
                        RawRefreshToken(
                            JwtBuilder(
                                subject = account.id.toString(),
                                issuedAt = LocalDateTime.now(),
                                expiresIn = jwtProperties.expirationThreshold + 1.hours,
                                secretKey = jwtProperties.refreshSecretKey,
                            ).build(),
                        )
                    refreshTokenJpaRepository.save(RefreshTokenBuilder(account.id, rawRefreshToken.value).build())

                    val actual = tokenService.reissue(rawRefreshToken)

                    actual.refreshToken.exists() shouldBe false
                }

                test("refreshToken 이 곧 만료되면 refresh token 도 갱신된다.") {
                    val account = accountJpaRepository.save(AccountBuilder().build())
                    val rawRefreshToken =
                        RawRefreshToken(
                            JwtBuilder(
                                subject = account.id.toString(),
                                issuedAt = LocalDateTime.now(),
                                expiresIn = jwtProperties.expirationThreshold - 1.seconds,
                                secretKey = jwtProperties.refreshSecretKey,
                            ).build(),
                        )
                    refreshTokenJpaRepository.save(RefreshTokenBuilder(account.id, rawRefreshToken.value).build())

                    val actual = tokenService.reissue(rawRefreshToken)

                    actual.refreshToken.exists() shouldBe true
                    actual.refreshToken.value shouldNotBe rawRefreshToken.value
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val account = accountJpaRepository.save(AccountBuilder(status = AccountStatus.DISABLED).build())
                    val rawRefreshToken =
                        RawRefreshToken(JwtBuilder(subject = account.id.toString(), secretKey = jwtProperties.refreshSecretKey).build())
                    refreshTokenJpaRepository.save(RefreshTokenBuilder(account.id, rawRefreshToken.value).build())

                    shouldThrow<AuthException> { tokenService.reissue(rawRefreshToken) }
                        .errorType shouldBe ACCOUNT_DISABLED_ERROR
                }

                test("refreshToken 이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { tokenService.reissue(RawRefreshToken("INVALID")) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }

                test("refreshToken 의 만료 시간이 지났으면 실패한다.") {
                    val account = accountJpaRepository.save(AccountBuilder().build())
                    val rawRefreshToken =
                        RawRefreshToken(
                            JwtBuilder(
                                subject = account.id.toString(),
                                issuedAt = LocalDateTime.now(),
                                expiresIn = Duration.ZERO,
                                secretKey = jwtProperties.refreshSecretKey,
                            ).build(),
                        )
                    refreshTokenJpaRepository.save(RefreshTokenBuilder(account.id, rawRefreshToken.value).build())

                    shouldThrow<AuthException> { tokenService.reissue(rawRefreshToken) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }

                test("refreshToken 이 만료되었으면 실패한다.") {
                    val account = accountJpaRepository.save(AccountBuilder().build())
                    val rawRefreshToken =
                        RawRefreshToken(JwtBuilder(subject = account.id.toString(), secretKey = jwtProperties.refreshSecretKey).build())
                    refreshTokenJpaRepository.save(RefreshTokenBuilder(account.id, rawRefreshToken.value, TokenStatus.EXPIRED).build())

                    shouldThrow<AuthException> { tokenService.reissue(rawRefreshToken) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }

                test("refreshToken 이 존재하지 않으면 실패한다.") {
                    shouldThrow<AuthException> {
                        tokenService.reissue(RawRefreshToken(JwtBuilder(secretKey = jwtProperties.refreshSecretKey).build()))
                    }.errorType shouldBe TOKEN_NOT_FOUND_ERROR
                }
            }

            context("accessToken 파싱") {
                test("성공") {
                    val account = accountJpaRepository.save(AccountBuilder().build())
                    val rawAccessToken =
                        RawAccessToken(JwtBuilder(subject = account.id.toString(), secretKey = jwtProperties.accessSecretKey).build())

                    val actual = tokenService.parse(rawAccessToken)

                    actual.id shouldBe account.id
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val account = accountJpaRepository.save(AccountBuilder(status = AccountStatus.DISABLED).build())
                    val rawAccessToken =
                        RawAccessToken(JwtBuilder(subject = account.id.toString(), secretKey = jwtProperties.accessSecretKey).build())

                    shouldThrow<AuthException> { tokenService.parse(rawAccessToken) }
                        .errorType shouldBe ACCOUNT_DISABLED_ERROR
                }

                test("accessToken 이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { tokenService.parse(RawAccessToken("INVALID")) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }

                test("accessToken 의 만료 시간이 지났으면 실패한다.") {
                    val account = accountJpaRepository.save(AccountBuilder().build())
                    val rawAccessToken =
                        RawAccessToken(
                            JwtBuilder(
                                subject = account.id.toString(),
                                issuedAt = LocalDateTime.now(),
                                expiresIn = Duration.ZERO,
                                secretKey = jwtProperties.accessSecretKey,
                            ).build(),
                        )
                    refreshTokenJpaRepository.save(RefreshTokenBuilder(account.id, rawAccessToken.value).build())

                    shouldThrow<AuthException> { tokenService.parse(rawAccessToken) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }
            }
        },
    )
