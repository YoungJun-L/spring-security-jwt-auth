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
                    val newRefreshToken = NewRefreshTokenBuilder().build()
                    refreshTokenJpaRepository.save(
                        RefreshTokenEntityBuilder(
                            newRefreshToken.userId,
                            "previousToken",
                        ).build(),
                    )

                    val actual = refreshTokenWriter.replace(newRefreshToken)

                    actual.refreshToken shouldBe newRefreshToken.refreshToken
                }

                test("이전 refreshToken 은 제거된다.") {
                    val newRefreshToken = NewRefreshTokenBuilder().build()
                    val refreshTokenEntity =
                        refreshTokenJpaRepository.save(RefreshTokenEntityBuilder(newRefreshToken.userId).build())

                    refreshTokenWriter.replace(newRefreshToken)

                    refreshTokenJpaRepository.findByIdOrNull(refreshTokenEntity.id) shouldBe null
                }
            }

            context("refreshToken 값 변경") {
                test("성공") {
                    val newRefreshToken = NewRefreshTokenBuilder().build()
                    refreshTokenJpaRepository.save(RefreshTokenEntityBuilder(newRefreshToken.userId).build())

                    val actual = refreshTokenWriter.update(newRefreshToken)

                    actual.refreshToken shouldBe newRefreshToken.refreshToken
                }
            }
        },
    )
