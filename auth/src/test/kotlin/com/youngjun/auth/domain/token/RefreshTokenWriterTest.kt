package com.youngjun.auth.domain.token

import com.youngjun.auth.infra.db.RefreshTokenJpaRepository
import com.youngjun.auth.support.DomainTest
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

            context("교체") {
                test("성공") {
                    val parsedRefreshToken = ParsedRefreshTokenBuilder().build()

                    val actual = refreshTokenWriter.replace(parsedRefreshToken)

                    refreshTokenJpaRepository.findByIdOrNull(actual.id)!!.value shouldBe parsedRefreshToken.value
                }

                test("이전 refresh token 은 제거된다.") {
                    val refreshToken = refreshTokenJpaRepository.save(RefreshTokenBuilder().build())

                    refreshTokenWriter.replace(ParsedRefreshTokenBuilder(refreshToken.userId).build())

                    refreshTokenJpaRepository.findByIdOrNull(refreshToken.id) shouldBe null
                }
            }

            context("값 변경") {
                test("성공") {
                    val refreshToken = refreshTokenJpaRepository.save(RefreshTokenBuilder().build())
                    val parsedRefreshToken = ParsedRefreshTokenBuilder(refreshToken.userId).build()

                    refreshTokenWriter.update(parsedRefreshToken)

                    refreshTokenJpaRepository.findByIdOrNull(refreshToken.id)!!.value shouldBe parsedRefreshToken.value
                }

                test("refreshToken 이 비어있으면 저장된 토큰 값이 변경되지 않는다.") {
                    val refreshToken = refreshTokenJpaRepository.save(RefreshTokenBuilder().build())

                    refreshTokenWriter.update(ParsedRefreshToken.Empty)

                    refreshTokenJpaRepository.findByIdOrNull(refreshToken.id)!!.value shouldBe refreshToken.value
                }
            }
        },
    )
