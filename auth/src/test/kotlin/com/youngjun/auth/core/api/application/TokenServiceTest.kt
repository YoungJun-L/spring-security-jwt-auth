package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.api.support.ApplicationTest
import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.api.support.error.ErrorType.ACCOUNT_DISABLED_ERROR
import com.youngjun.auth.core.api.support.error.ErrorType.TOKEN_EXPIRED_ERROR
import com.youngjun.auth.core.api.support.error.ErrorType.TOKEN_INVALID_ERROR
import com.youngjun.auth.core.domain.account.AccountBuilder
import com.youngjun.auth.core.domain.account.AccountStatus
import com.youngjun.auth.core.domain.token.JwtBuilder
import com.youngjun.auth.core.domain.token.RefreshTokenBuilder
import com.youngjun.auth.core.domain.token.SecretKeyHolder
import com.youngjun.auth.storage.db.core.account.AccountEntityBuilder
import com.youngjun.auth.storage.db.core.account.AccountJpaRepository
import com.youngjun.auth.storage.db.core.token.TokenEntity
import com.youngjun.auth.storage.db.core.token.TokenJpaRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
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

                test("이전 refresh token 은 제거된다.") {
                    val userId = 1L
                    val account = AccountBuilder(id = userId).build()
                    val previousToken = JwtBuilder().build()
                    tokenJpaRepository.save(TokenEntity(userId, previousToken))

                    tokenService.issue(account)

                    val actual = tokenJpaRepository.findByUserId(userId)
                    actual shouldHaveSize 1
                    actual[0] shouldNotBe previousToken
                }
            }

            context("토큰 재발급") {
                test("성공") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val refreshToken = JwtBuilder(secretKey = secretKeyHolder.get()).build()
                    tokenJpaRepository.save(TokenEntity(accountEntity.id, refreshToken))

                    val actual = tokenService.reissue(RefreshTokenBuilder(refreshToken).build())

                    actual.userId shouldBe accountEntity.id
                }

                test("refresh token 의 만료 기간이 1주일 이상 남았으면 refresh token 은 갱신되지 않는다.") {
                    TODO()
                }

                test("refresh token 의 만료 기간이 1주일 미만 남았으면 refresh token 도 갱신된다.") {
                    TODO()
                }

                test("refresh token 이 갱신되면 이전 토큰은 제거된다.") {
                    TODO()
                }

                test("서비스 이용이 제한된 유저이면 실패한다.") {
                    val accountEntity =
                        accountJpaRepository.save(AccountEntityBuilder(status = AccountStatus.DISABLED).build())
                    val refreshToken =
                        JwtBuilder(secretKey = secretKeyHolder.get(), subject = accountEntity.username).build()
                    tokenJpaRepository.save(TokenEntity(accountEntity.id, refreshToken))

                    shouldThrow<AuthException> { tokenService.reissue(RefreshTokenBuilder(refreshToken).build()) }
                        .errorType shouldBe ACCOUNT_DISABLED_ERROR
                }

                test("토큰이 유효하지 않으면 실패한다.") {
                    val refreshToken = "invalid"
                    shouldThrow<AuthException> { tokenService.reissue(RefreshTokenBuilder(refreshToken).build()) }
                        .errorType shouldBe TOKEN_INVALID_ERROR
                }

                test("토큰이 만료되었으면 실패한다.") {
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder().build())
                    val refreshToken =
                        JwtBuilder(
                            secretKey = secretKeyHolder.get(),
                            subject = accountEntity.username,
                            expiresInMilliseconds = 0,
                        ).build()
                    tokenJpaRepository.save(TokenEntity(accountEntity.id, refreshToken))

                    shouldThrow<AuthException> { tokenService.reissue(RefreshTokenBuilder(refreshToken).build()) }
                        .errorType shouldBe TOKEN_EXPIRED_ERROR
                }
            }
        },
    )
