package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.api.security.JwtProperties
import com.youngjun.auth.core.domain.account.AccountBuilder
import com.youngjun.auth.core.domain.account.AccountStatus
import com.youngjun.auth.core.domain.support.EPOCH
import com.youngjun.auth.core.domain.support.days
import com.youngjun.auth.core.domain.support.seconds
import com.youngjun.auth.core.domain.token.AccessToken
import com.youngjun.auth.core.domain.token.JwtBuilder
import com.youngjun.auth.core.domain.token.RefreshToken
import com.youngjun.auth.core.domain.token.TokenStatus
import com.youngjun.auth.core.support.ApplicationTest
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.ACCOUNT_DISABLED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_INVALID_ERROR
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
                    val account = AccountBuilder().build()

                    val actual = tokenService.issue(account)

                    actual.userId shouldBe account.id
                }

                test("이전 refresh token 은 교체된다.") {
                    val account = AccountBuilder().build()
                    val refreshTokenEntity = refreshTokenJpaRepository.save(RefreshTokenEntityBuilder(account.id).build())

                    val actual = tokenService.issue(account)

                    actual.refreshToken.value shouldNotBe refreshTokenEntity.token
                }
            }

            context("토큰 재발급") {
                test("성공") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val refreshToken = RefreshToken(JwtBuilder(secretKey = jwtProperties.refreshSecretKey).build())
                    refreshTokenJpaRepository.save(RefreshTokenEntityBuilder(accountEntity.id, refreshToken.value).build())

                    val actual = tokenService.reissue(refreshToken)

                    actual.userId shouldBe accountEntity.id
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val accountEntity =
                        accountJpaRepository.save(AccountEntityBuilder(status = AccountStatus.DISABLED).build())
                    val refreshToken =
                        RefreshToken(
                            JwtBuilder(
                                secretKey = jwtProperties.refreshSecretKey,
                                subject = accountEntity.id.toString(),
                            ).build(),
                        )
                    refreshTokenJpaRepository.save(RefreshTokenEntityBuilder(accountEntity.id, refreshToken.value).build())

                    shouldThrow<AuthException> { tokenService.reissue(refreshToken) }
                        .errorType shouldBe ACCOUNT_DISABLED_ERROR
                }

                test("refreshToken 이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { tokenService.reissue(RefreshToken("INVALID")) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }

                test("refreshToken 의 만료 시간이 지났으면 실패한다.") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val refreshToken =
                        RefreshToken(
                            JwtBuilder(
                                secretKey = jwtProperties.refreshSecretKey,
                                subject = accountEntity.id.toString(),
                                expiresIn = Duration.ZERO,
                            ).build(),
                        )
                    refreshTokenJpaRepository.save(RefreshTokenEntityBuilder(accountEntity.id, refreshToken.value).build())

                    shouldThrow<AuthException> { tokenService.reissue(refreshToken) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }

                test("refreshToken 이 만료되었으면 실패한다.") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val refreshToken =
                        RefreshToken(
                            JwtBuilder(
                                secretKey = jwtProperties.refreshSecretKey,
                                subject = accountEntity.id.toString(),
                            ).build(),
                        )
                    refreshTokenJpaRepository.save(
                        RefreshTokenEntityBuilder(
                            accountEntity.id,
                            refreshToken.value,
                            TokenStatus.EXPIRED,
                        ).build(),
                    )

                    shouldThrow<AuthException> { tokenService.reissue(refreshToken) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }
            }

            context("accessToken 파싱") {
                test("성공") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val accessToken = AccessToken(JwtBuilder(secretKey = jwtProperties.accessSecretKey).build())

                    val actual = tokenService.parse(accessToken)

                    actual.id shouldBe accountEntity.id
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val accountEntity =
                        accountJpaRepository.save(AccountEntityBuilder(status = AccountStatus.DISABLED).build())
                    val accessToken =
                        AccessToken(
                            JwtBuilder(
                                secretKey = jwtProperties.accessSecretKey,
                                subject = accountEntity.id.toString(),
                            ).build(),
                        )

                    shouldThrow<AuthException> { tokenService.parse(accessToken) }
                        .errorType shouldBe ACCOUNT_DISABLED_ERROR
                }

                test("accessToken 이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { tokenService.parse(AccessToken("INVALID")) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }

                test("accessToken 의 만료 시간이 지났으면 실패한다.") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val accessToken =
                        AccessToken(
                            JwtBuilder(
                                secretKey = jwtProperties.accessSecretKey,
                                subject = accountEntity.id.toString(),
                                expiresIn = Duration.ZERO,
                            ).build(),
                        )
                    refreshTokenJpaRepository.save(RefreshTokenEntityBuilder(accountEntity.id, accessToken.value).build())

                    shouldThrow<AuthException> { tokenService.parse(accessToken) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }
            }
        },
    )
