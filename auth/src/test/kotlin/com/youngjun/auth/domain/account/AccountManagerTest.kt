package com.youngjun.auth.domain.account

import com.youngjun.auth.domain.token.RefreshTokenEntityBuilder
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

                    val actual = accountManager.logout(account)

                    actual.id shouldBe account.id
                    actual.status shouldBe AccountStatus.LOGOUT
                }

                test("저장된 계정 상태가 LOGOUT 으로 변경된다.") {
                    val account = accountJpaRepository.save(AccountBuilder().build())

                    accountManager.logout(account)

                    accountJpaRepository.findByIdOrNull(account.id)!!.status shouldBe AccountStatus.LOGOUT
                }

                test("refreshToken 이 만료된다.") {
                    val account = accountJpaRepository.save(AccountBuilder().build())
                    val refreshToken = refreshTokenJpaRepository.save(RefreshTokenEntityBuilder(account.id).build())

                    accountManager.logout(account)

                    refreshTokenJpaRepository.findByIdOrNull(refreshToken.id)!!.status shouldBe TokenStatus.EXPIRED
                }
            }

            context("로그인") {
                test("성공") {
                    val account = accountJpaRepository.save(AccountBuilder(status = AccountStatus.LOGOUT).build())

                    val actual = accountManager.login(account)

                    actual.id shouldBe account.id
                    actual.status shouldBe AccountStatus.ENABLED
                }

                test("저장된 계정 상태가 ENABLED 으로 변경된다.") {
                    val account = accountJpaRepository.save(AccountBuilder(status = AccountStatus.LOGOUT).build())

                    accountManager.login(account)

                    accountJpaRepository.findByIdOrNull(account.id)!!.status shouldBe AccountStatus.ENABLED
                }
            }
        },
    )
