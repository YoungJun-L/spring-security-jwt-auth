package com.youngjun.auth.domain.account

import com.youngjun.auth.domain.token.RefreshTokenBuilder
import com.youngjun.auth.domain.token.TokenStatus
import com.youngjun.auth.infra.db.AccountJpaRepository
import com.youngjun.auth.infra.db.RefreshTokenJpaRepository
import com.youngjun.auth.support.DomainTest
import io.kotest.core.spec.IsolationMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.shouldBe
import org.springframework.data.repository.findByIdOrNull

@DomainTest
class AccountManagerTest(
    private val accountManager: AccountManager,
    private val accountJpaRepository: AccountJpaRepository,
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository,
) : FunSpec(
        {
            extensions(SpringExtension)
            isolationMode = IsolationMode.InstancePerLeaf

            context("로그아웃") {
                test("성공") {
                    val account = accountJpaRepository.save(AccountBuilder().build())

                    accountManager.logout(account)

                    accountJpaRepository.findByIdOrNull(account.id)!!.status shouldBe AccountStatus.LOGOUT
                }

                test("refreshToken 이 만료된다.") {
                    val account = accountJpaRepository.save(AccountBuilder().build())
                    val refreshToken = refreshTokenJpaRepository.save(RefreshTokenBuilder(account.id).build())

                    accountManager.logout(account)

                    refreshTokenJpaRepository.findByIdOrNull(refreshToken.id)!!.status shouldBe TokenStatus.EXPIRED
                }
            }

            context("로그인") {
                test("성공") {
                    val account = accountJpaRepository.save(AccountBuilder(status = AccountStatus.LOGOUT).build())

                    accountManager.login(account)

                    accountJpaRepository.findByIdOrNull(account.id)!!.status shouldBe AccountStatus.ENABLED
                }
            }
        },
    )
