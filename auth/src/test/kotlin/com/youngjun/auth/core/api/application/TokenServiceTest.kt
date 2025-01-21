package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.domain.account.AccountBuilder
import com.youngjun.auth.core.domain.account.AccountStatus
import com.youngjun.auth.core.domain.token.JwtBuilder
import com.youngjun.auth.core.domain.token.SecretKeyHolder
import com.youngjun.auth.core.support.ApplicationTest
import com.youngjun.auth.core.support.error.AuthException
import com.youngjun.auth.core.support.error.ErrorType.ACCOUNT_DISABLED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import com.youngjun.auth.core.support.error.ErrorType.TOKEN_INVALID_ERROR
import com.youngjun.auth.storage.db.core.account.AccountEntityBuilder
import com.youngjun.auth.storage.db.core.account.AccountJpaRepository
import com.youngjun.auth.storage.db.core.token.TokenEntity
import com.youngjun.auth.storage.db.core.token.TokenEntityBuilder
import com.youngjun.auth.storage.db.core.token.TokenJpaRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@ApplicationTest
class TokenServiceTest(
    private val tokenService: TokenService,
    private val tokenJpaRepository: TokenJpaRepository,
    private val accountJpaRepository: AccountJpaRepository,
    private val secretKeyHolder: SecretKeyHolder,
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
                    val tokenEntity = tokenJpaRepository.save(TokenEntityBuilder(account.id).build())

                    val actual = tokenService.issue(account)

                    actual.refreshToken shouldNotBe tokenEntity.refreshToken
                }
            }

            context("토큰 재발급") {
                test("성공") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val refreshToken = JwtBuilder(secretKey = secretKeyHolder.get()).build()
                    tokenJpaRepository.save(TokenEntityBuilder(accountEntity.id, refreshToken).build())

                    val actual = tokenService.reissue(refreshToken)

                    actual.userId shouldBe accountEntity.id
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val accountEntity =
                        accountJpaRepository.save(AccountEntityBuilder(status = AccountStatus.DISABLED).build())
                    val refreshToken =
                        JwtBuilder(secretKey = secretKeyHolder.get(), subject = accountEntity.id.toString()).build()
                    tokenJpaRepository.save(TokenEntity(accountEntity.id, refreshToken))

                    shouldThrow<AuthException> { tokenService.reissue(refreshToken) }
                        .errorType shouldBe ACCOUNT_DISABLED_ERROR
                }

                test("토큰이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { tokenService.reissue("invalid") }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }

                test("토큰이 만료되었으면 실패한다.") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val refreshToken =
                        JwtBuilder(
                            secretKey = secretKeyHolder.get(),
                            subject = accountEntity.id.toString(),
                            expiresInMilliseconds = 0,
                        ).build()
                    tokenJpaRepository.save(TokenEntity(accountEntity.id, refreshToken))

                    shouldThrow<AuthException> { tokenService.reissue(refreshToken) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }
            }

            context("accessToken 파싱") {
                test("성공") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val accessToken = JwtBuilder(secretKey = secretKeyHolder.get()).build()

                    val actual = tokenService.parse(accessToken)

                    actual.id shouldBe accountEntity.id
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val accountEntity =
                        accountJpaRepository.save(AccountEntityBuilder(status = AccountStatus.DISABLED).build())
                    val accessToken =
                        JwtBuilder(secretKey = secretKeyHolder.get(), subject = accountEntity.id.toString()).build()

                    shouldThrow<AuthException> { tokenService.parse(accessToken) }
                        .errorType shouldBe ACCOUNT_DISABLED_ERROR
                }

                test("토큰이 유효하지 않으면 실패한다.") {
                    shouldThrow<AuthException> { tokenService.parse("invalid") }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }

                test("토큰이 만료되었으면 실패한다.") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val accessToken =
                        JwtBuilder(
                            secretKey = secretKeyHolder.get(),
                            subject = accountEntity.id.toString(),
                            expiresInMilliseconds = 0,
                        ).build()
                    tokenJpaRepository.save(TokenEntity(accountEntity.id, accessToken))

                    shouldThrow<AuthException> { tokenService.parse(accessToken) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }
            }
        },
    )
