package com.youngjun.auth.domain.token

import com.youngjun.auth.domain.account.AccountBuilder
import com.youngjun.auth.infra.db.RefreshTokenJpaRepository
import com.youngjun.auth.support.DomainContextTest
import com.youngjun.auth.support.error.AuthException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.data.repository.findByIdOrNull

@DomainContextTest
class RefreshTokenStoreTest(
    private val refreshTokenStore: RefreshTokenStore,
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("이전 refreshToken 과 교체") {
                test("성공") {
                    val refreshToken = refreshTokenJpaRepository.save(RefreshTokenBuilder().build())
                    val parsedRefreshToken = ParsedRefreshTokenBuilder(refreshToken.userId).build()

                    refreshTokenStore.replace(parsedRefreshToken)

                    refreshTokenJpaRepository.findByUserId(parsedRefreshToken.userId)!!.value shouldBe parsedRefreshToken.value
                }
            }

            context("존재하면 만료") {
                test("존재하는 경우 만료한다.") {
                    val account = AccountBuilder().build()
                    val refreshToken = refreshTokenJpaRepository.save(RefreshTokenBuilder(account.id).build())

                    refreshTokenStore.expireIfExists(account)

                    refreshTokenJpaRepository.findByIdOrNull(refreshToken.id)!!.status shouldBe TokenStatus.EXPIRED
                }

                test("존재하지 않는 경우 실패하지 않는다.") {
                    shouldNotThrow<AuthException> { refreshTokenStore.expireIfExists(AccountBuilder().build()) }
                }
            }
        },
    )
