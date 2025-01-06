package com.youngjun.auth.core.api.application

import com.youngjun.auth.core.api.support.ApplicationTest
import com.youngjun.auth.core.domain.auth.AuthBuilder
import com.youngjun.auth.core.domain.token.RefreshTokenBuilder
import com.youngjun.auth.core.domain.token.TokenParser
import com.youngjun.auth.core.domain.token.TokenService
import com.youngjun.auth.core.storage.db.core.auth.AuthEntityBuilder
import com.youngjun.auth.storage.db.core.auth.AuthJpaRepository
import com.youngjun.auth.storage.db.core.token.TokenEntity
import com.youngjun.auth.storage.db.core.token.TokenJpaRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.extensions.time.withConstantNow
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

@ApplicationTest
class TokenServiceKtTest(
    private val tokenService: TokenService,
    private val tokenJpaRepository: TokenJpaRepository,
    private val authJpaRepository: AuthJpaRepository,
    private val tokenParser: TokenParser,
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

                test("access token 은 30분간 유효하다.") {
                    val now = LocalDateTime.now()
                    withConstantNow(now) {
                        val auth = AuthBuilder().build()

                        val actual = tokenService.issue(auth)

                        actual.accessTokenExpiresIn shouldBe now.toEpochSecond(ZoneOffset.UTC) + 30.minutes.inWholeMilliseconds
                    }
                }

                test("refresh token 은 30일간 유효하다.") {
                    val now = LocalDateTime.now()
                    withConstantNow(now) {
                        val auth = AuthBuilder().build()

                        val actual = tokenService.issue(auth)

                        actual.refreshTokenExpiresIn shouldBe now.toEpochSecond(ZoneOffset.UTC) + 30.days.inWholeMilliseconds
                    }
                }

                test("이전 refresh token은 제거된다") {
                    val previousToken = "refreshToken"
                    val authId = 1L
                    val auth = AuthBuilder(id = authId).build()
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
                    val previousToken = RefreshTokenBuilder().build()
                    tokenJpaRepository.save(TokenEntity(authEntity.id, previousToken.value))
                    every { tokenParser.verify(previousToken) } just Runs

                    val actual = tokenService.reissue(previousToken)

                    actual.authId shouldBe authEntity.id
                }
            }
        },
    )
