package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.api.support.ApplicationTest
import com.youngjun.auth.core.api.support.error.AuthException
import com.youngjun.auth.core.domain.auth.AuthBuilder
import com.youngjun.auth.core.domain.auth.AuthStatus
import com.youngjun.auth.core.domain.token.RefreshTokenBuilder
import com.youngjun.auth.core.domain.token.buildJwt
import com.youngjun.auth.core.storage.db.core.auth.AuthEntityBuilder
import com.youngjun.auth.storage.db.core.auth.AuthJpaRepository
import com.youngjun.auth.storage.db.core.token.TokenEntity
import com.youngjun.auth.storage.db.core.token.TokenJpaRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.beans.factory.annotation.Value
import java.time.LocalDateTime.now

@ApplicationTest
class TokenServiceTest(
    private val tokenService: TokenService,
    private val tokenJpaRepository: TokenJpaRepository,
    private val authJpaRepository: AuthJpaRepository,
    @Value("\${spring.security.jwt.secret-key}") private val secretKey: String,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("토큰 발급") {
                test("성공") {
                    val auth = AuthBuilder().build()

                    val actual = tokenService.issue(auth)

                    actual.authId shouldBe auth.id
                }

                test("이전 refresh token 은 제거된다.") {
                    val authId = 1L
                    val auth = AuthBuilder(id = authId).build()
                    val previousToken = buildJwt(secretKey = secretKey)
                    tokenJpaRepository.save(TokenEntity(authId, previousToken))

                    tokenService.issue(auth)

                    val actual = tokenJpaRepository.findByAuthId(authId)
                    actual shouldHaveSize 1
                    actual[0] shouldNotBe previousToken
                }
            }

            context("토큰 재발급") {
                test("성공") {
                    val authEntity = authJpaRepository.save(AuthEntityBuilder().build())
                    val refreshToken = buildJwt(secretKey = secretKey)
                    tokenJpaRepository.save(TokenEntity(authEntity.id, refreshToken))

                    val actual = tokenService.reissue(RefreshTokenBuilder(refreshToken).build())

                    actual.authId shouldBe authEntity.id
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
                    val authEntity = authJpaRepository.save(AuthEntityBuilder(status = AuthStatus.DISABLED).build())
                    val refreshToken = buildJwt(secretKey = secretKey, subject = authEntity.username)
                    tokenJpaRepository.save(TokenEntity(authEntity.id, refreshToken))

                    shouldThrow<AuthException> { tokenService.reissue(RefreshTokenBuilder(refreshToken).build()) }
                }

                test("토큰이 유효하지 않으면 실패한다.") {
                    val refreshToken = "invalid"
                    shouldThrow<AuthException> { tokenService.reissue(RefreshTokenBuilder(refreshToken).build()) }
                }

                test("토큰이 만료되었으면 실패한다.") {
                    val authEntity = authJpaRepository.save(AuthEntityBuilder().build())
                    val refreshToken =
                        buildJwt(
                            secretKey = secretKey,
                            subject = authEntity.username,
                            issuedAt = now(),
                            expiresInSeconds = 0,
                        )
                    tokenJpaRepository.save(TokenEntity(authEntity.id, refreshToken))

                    shouldThrow<AuthException> { tokenService.reissue(RefreshTokenBuilder(refreshToken).build()) }
                }
            }
        },
    )
