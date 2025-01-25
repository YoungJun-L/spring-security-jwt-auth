package com.youngjun.auth.core.domain.token

import com.youngjun.auth.core.support.DomainTest
import com.youngjun.auth.storage.db.core.token.RefreshTokenEntityBuilder
import com.youngjun.auth.storage.db.core.token.RefreshTokenJpaRepository
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.data.repository.findByIdOrNull

@DomainTest
class RefreshTokenWriterTest(
    private val refreshTokenWriter: RefreshTokenWriter,
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("refreshToken 교체") {
                test("성공") {
                    val tokenPairDetails = TokenPairDetailsBuilder().build()
                    refreshTokenJpaRepository.save(
                        RefreshTokenEntityBuilder(
                            userId = tokenPairDetails.userId,
                            token = "PREVIOUS",
                        ).build(),
                    )

                    val actual = refreshTokenWriter.replace(tokenPairDetails)

                    actual.refreshToken shouldBe tokenPairDetails.refreshToken
                }

                test("이전 refreshToken 은 제거된다.") {
                    val tokenPairDetails = TokenPairDetailsBuilder().build()
                    val refreshTokenEntity =
                        refreshTokenJpaRepository.save(RefreshTokenEntityBuilder(tokenPairDetails.userId).build())

                    refreshTokenWriter.replace(tokenPairDetails)

                    refreshTokenJpaRepository.findByIdOrNull(refreshTokenEntity.id) shouldBe null
                }
            }

            context("refreshToken 값 변경") {
                test("성공") {
                    val tokenPairDetails = TokenPairDetailsBuilder().build()
                    refreshTokenJpaRepository.save(RefreshTokenEntityBuilder(tokenPairDetails.userId).build())

                    val actual = refreshTokenWriter.update(tokenPairDetails)

                    actual.refreshToken shouldBe tokenPairDetails.refreshToken
                }
            }
        },
    )
