package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.api.security.JwtProperties
import com.youngjun.auth.core.domain.account.AccountBuilder
import com.youngjun.auth.core.domain.support.hours
import com.youngjun.auth.core.domain.support.seconds
import com.youngjun.auth.core.support.DomainTest
import com.youngjun.auth.storage.db.core.token.RefreshTokenEntityBuilder
import com.youngjun.auth.storage.db.core.token.RefreshTokenJpaRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.data.repository.findByIdOrNull

@DomainTest
class TokenPairGeneratorTest(
    private val tokenPairGenerator: TokenPairGenerator,
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
    private val jwtProperties: JwtProperties,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("발급") {
                test("성공") {
                    val userId = 1L

                    val actual = tokenPairGenerator.issue(AccountBuilder(id = userId).build())

                    actual.userId shouldBe userId
                }

                test("이전 refreshToken 은 교체된다.") {
                    val userId = 1L

                    val expected = tokenPairGenerator.issue(AccountBuilder(id = userId).build())

                    refreshTokenJpaRepository.findByUserId(userId)!!.token shouldBe expected.refreshToken.value
                }
            }

            context("재발급") {
                test("성공") {
                    val userId = 1L

                    val actual =
                        tokenPairGenerator.reissue(
                            AccountBuilder(id = userId).build(),
                            ParsedRefreshTokenBuilder(userId).build(),
                        )

                    actual.userId shouldBe userId
                }

                test("refreshToken 이 갱신되면 저장된 토큰의 값이 변경된다.") {
                    val userId = 1L
                    val parsedRefreshToken =
                        ParsedRefreshTokenBuilder(
                            userId = userId,
                            expiresIn = jwtProperties.expirationThreshold - 1.seconds,
                        ).build()
                    val refreshTokenEntity =
                        refreshTokenJpaRepository.save(RefreshTokenEntityBuilder(userId, parsedRefreshToken.value).build())

                    val expected = tokenPairGenerator.reissue(AccountBuilder(id = userId).build(), parsedRefreshToken)

                    refreshTokenJpaRepository.findByIdOrNull(refreshTokenEntity.id)!!.token shouldBe expected.refreshToken.value
                }

                test("refreshToken 이 갱신되지 않으면 저장된 토큰의 값이 변경되지 않는다.") {
                    val userId = 1L
                    val parsedRefreshToken =
                        ParsedRefreshTokenBuilder(
                            userId = userId,
                            expiresIn = jwtProperties.expirationThreshold + 1.hours,
                        ).build()
                    val refreshTokenEntity =
                        refreshTokenJpaRepository.save(RefreshTokenEntityBuilder(userId, parsedRefreshToken.value).build())

                    tokenPairGenerator.reissue(AccountBuilder(id = userId).build(), parsedRefreshToken)

                    refreshTokenJpaRepository.findByIdOrNull(refreshTokenEntity.id)!!.token shouldBe parsedRefreshToken.value
                }
            }
        },
    )
