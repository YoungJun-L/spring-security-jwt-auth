package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.infra.db.RefreshTokenJpaRepository
import com.youngjun.auth.support.DomainContextTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.data.repository.findByIdOrNull

@DomainContextTest
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

                    refreshTokenWriter.replaceIfNotEmpty(parsedRefreshToken)

                    refreshTokenJpaRepository.findByUserId(parsedRefreshToken.userId)!!.value shouldBe parsedRefreshToken.value
                }

                test("refreshToken 이 비어있지 않으면 이전 refresh token 은 제거된다.") {
                    val refreshToken = refreshTokenJpaRepository.save(RefreshTokenBuilder().build())

                    refreshTokenWriter.replaceIfNotEmpty(ParsedRefreshTokenBuilder(refreshToken.userId).build())

                    refreshTokenJpaRepository.findByIdOrNull(refreshToken.id) shouldBe null
                }

                test("refreshToken 이 비어있으면 이전 토큰은 제거되지 않는다.") {
                    val refreshToken = refreshTokenJpaRepository.save(RefreshTokenBuilder().build())

                    refreshTokenWriter.replaceIfNotEmpty(ParsedRefreshToken.Empty)

                    refreshTokenJpaRepository.findByIdOrNull(refreshToken.id)!!.value shouldBe refreshToken.value
                }
            }

            context("refreshToken 만료") {
                test("성공") {
                    val account = AccountBuilder().build()
                    val refreshToken = refreshTokenJpaRepository.save(RefreshTokenBuilder(account.id).build())

                    refreshTokenWriter.expire(account)

                    refreshTokenJpaRepository.findByIdOrNull(refreshToken.id)!!.status shouldBe TokenStatus.EXPIRED
                }
            }
        },
    )
