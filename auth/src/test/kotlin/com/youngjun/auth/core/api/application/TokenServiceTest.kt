package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.api.security.JwtProperties
import com.youngjun.auth.core.domain.account.AccountStatus
import com.youngjun.auth.core.domain.support.hours
import com.youngjun.auth.core.domain.support.seconds
import com.youngjun.auth.core.domain.token.JwtBuilder
import com.youngjun.auth.core.domain.token.RawAccessToken
import com.youngjun.auth.core.domain.token.RawRefreshToken
import com.youngjun.auth.core.domain.token.TokenStatus
import com.youngjun.auth.core.support.ApplicationTest
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.ACCOUNT_DISABLED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_INVALID_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_NOT_FOUND_ERROR
import com.youngjun.auth.storage.db.core.account.AccountEntityBuilder
import com.youngjun.auth.storage.db.core.account.AccountJpaRepository
import com.youngjun.auth.storage.db.core.token.RefreshTokenEntityBuilder
import com.youngjun.auth.storage.db.core.token.RefreshTokenJpaRepository
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
                    val refreshTokenEntity = refreshTokenJpaRepository.save(RefreshTokenEntityBuilder(userId).build())

                    val actual = tokenService.issue(userId)

                    actual.refreshToken.value shouldNotBe refreshTokenEntity.token
                }
            }

            context("토큰 재발급") {
                test("성공") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val rawRefreshToken = RawRefreshToken(JwtBuilder(secretKey = jwtProperties.refreshSecretKey).build())
                    refreshTokenJpaRepository.save(
                        RefreshTokenEntityBuilder(
                            accountEntity.id,
                            rawRefreshToken.value,
                        ).build(),
                    )

                    val actual = tokenService.reissue(rawRefreshToken)

                    actual.userId shouldBe accountEntity.id
                }

                test("refreshToken 이 아직 만료되지 않았으면 refresh token 은 갱신되지 않는다.") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val rawRefreshToken =
                        RawRefreshToken(
                            JwtBuilder(
                                secretKey = jwtProperties.refreshSecretKey,
                                subject = accountEntity.id.toString(),
                                issuedAt = LocalDateTime.now(),
                                expiresIn = jwtProperties.expirationThreshold + 1.hours,
                            ).build(),
                        )
                    refreshTokenJpaRepository.save(
                        RefreshTokenEntityBuilder(
                            accountEntity.id,
                            rawRefreshToken.value,
                        ).build(),
                    )

                    val actual = tokenService.reissue(rawRefreshToken)

                    actual.refreshToken.exists() shouldBe false
                }

                test("refreshToken 이 곧 만료되면 refresh token 도 갱신된다.") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val rawRefreshToken =
                        RawRefreshToken(
                            JwtBuilder(
                                secretKey = jwtProperties.refreshSecretKey,
                                subject = accountEntity.id.toString(),
                                issuedAt = LocalDateTime.now(),
                                expiresIn = jwtProperties.expirationThreshold - 1.seconds,
                            ).build(),
                        )
                    refreshTokenJpaRepository.save(
                        RefreshTokenEntityBuilder(
                            accountEntity.id,
                            rawRefreshToken.value,
                        ).build(),
                    )

                    val actual = tokenService.reissue(rawRefreshToken)

                    actual.refreshToken.exists() shouldBe true
                    actual.refreshToken.value shouldNotBe rawRefreshToken.value
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val accountEntity =
                        accountJpaRepository.save(AccountEntityBuilder(status = AccountStatus.DISABLED).build())
                    val rawRefreshToken =
                        RawRefreshToken(
                            JwtBuilder(
                                secretKey = jwtProperties.refreshSecretKey,
                                subject = accountEntity.id.toString(),
                            ).build(),
                        )
                    refreshTokenJpaRepository.save(
                        RefreshTokenEntityBuilder(
                            accountEntity.id,
                            rawRefreshToken.value,
                        ).build(),
                    )

                    shouldThrow<AuthException> { tokenService.reissue(rawRefreshToken) }
                        .errorType shouldBe ACCOUNT_DISABLED_ERROR
                }

                test("refreshToken 이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { tokenService.reissue(RawRefreshToken("INVALID")) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }

                test("refreshToken 의 만료 시간이 지났으면 실패한다.") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val rawRefreshToken =
                        RawRefreshToken(
                            JwtBuilder(
                                secretKey = jwtProperties.refreshSecretKey,
                                subject = accountEntity.id.toString(),
                                issuedAt = LocalDateTime.now(),
                                expiresIn = Duration.ZERO,
                            ).build(),
                        )
                    refreshTokenJpaRepository.save(
                        RefreshTokenEntityBuilder(
                            accountEntity.id,
                            rawRefreshToken.value,
                        ).build(),
                    )

                    shouldThrow<AuthException> { tokenService.reissue(rawRefreshToken) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }

                test("refreshToken 이 만료되었으면 실패한다.") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val rawRefreshToken =
                        RawRefreshToken(
                            JwtBuilder(
                                secretKey = jwtProperties.refreshSecretKey,
                                subject = accountEntity.id.toString(),
                            ).build(),
                        )
                    refreshTokenJpaRepository.save(
                        RefreshTokenEntityBuilder(
                            accountEntity.id,
                            rawRefreshToken.value,
                            TokenStatus.EXPIRED,
                        ).build(),
                    )

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
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val rawAccessToken = RawAccessToken(JwtBuilder(secretKey = jwtProperties.accessSecretKey).build())

                    val actual = tokenService.parse(rawAccessToken)

                    actual.id shouldBe accountEntity.id
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val accountEntity =
                        accountJpaRepository.save(AccountEntityBuilder(status = AccountStatus.DISABLED).build())
                    val rawAccessToken =
                        RawAccessToken(
                            JwtBuilder(
                                secretKey = jwtProperties.accessSecretKey,
                                subject = accountEntity.id.toString(),
                            ).build(),
                        )

                    shouldThrow<AuthException> { tokenService.parse(rawAccessToken) }
                        .errorType shouldBe ACCOUNT_DISABLED_ERROR
                }

                test("accessToken 이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { tokenService.parse(RawAccessToken("INVALID")) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }

                test("accessToken 의 만료 시간이 지났으면 실패한다.") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val rawAccessToken =
                        RawAccessToken(
                            JwtBuilder(
                                secretKey = jwtProperties.accessSecretKey,
                                subject = accountEntity.id.toString(),
                                issuedAt = LocalDateTime.now(),
                                expiresIn = Duration.ZERO,
                            ).build(),
                        )
                    refreshTokenJpaRepository.save(
                        RefreshTokenEntityBuilder(
                            accountEntity.id,
                            rawAccessToken.value,
                        ).build(),
                    )

                    shouldThrow<AuthException> { tokenService.parse(rawAccessToken) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }
            }
        },
    )
