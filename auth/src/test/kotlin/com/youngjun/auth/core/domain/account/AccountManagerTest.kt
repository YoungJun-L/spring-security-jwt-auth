package com.youngjun.auth.core.domain.account

import com.youngjun.auth.core.domain.token.TokenStatus
import com.youngjun.auth.core.support.DomainTest
import com.youngjun.auth.storage.db.core.account.AccountEntityBuilder
import com.youngjun.auth.storage.db.core.account.AccountJpaRepository
import com.youngjun.auth.storage.db.core.token.RefreshTokenEntityBuilder
import com.youngjun.auth.storage.db.core.token.RefreshTokenJpaRepository
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
                    val account = AccountBuilder().build()
                    accountJpaRepository.save(AccountEntityBuilder(username = account.username).build())

                    val actual = accountManager.logout(account)

                    actual.id shouldBe account.id
                    actual.status shouldBe AccountStatus.LOGOUT
                }

                test("저장된 계정 상태가 LOGOUT 으로 변경된다.") {
                    val account = AccountBuilder().build()
                    val accountEntity = accountJpaRepository.save(AccountEntityBuilder(username = account.username).build())

                    accountManager.logout(account)

                    accountJpaRepository.findByIdOrNull(accountEntity.id)!!.status shouldBe AccountStatus.LOGOUT
                }

                test("refreshToken 이 만료된다.") {
                    val account = AccountBuilder().build()
                    accountJpaRepository.save(AccountEntityBuilder(username = account.username).build())
                    val refreshTokenEntity = refreshTokenJpaRepository.save(RefreshTokenEntityBuilder(account.id).build())

                    accountManager.logout(account)

                    refreshTokenJpaRepository.findByIdOrNull(refreshTokenEntity.id)!!.status shouldBe TokenStatus.EXPIRED
                }
            }
            context("로그인") {
                test("성공") {
                    val account = AccountBuilder(status = AccountStatus.LOGOUT).build()
                    accountJpaRepository.save(
                        AccountEntityBuilder(
                            username = account.username,
                            status = account.status,
                        ).build(),
                    )

                    val actual = accountManager.login(account)

                    actual.id shouldBe account.id
                    actual.status shouldBe AccountStatus.ENABLED
                }

                test("저장된 계정 상태가 ENABLED 으로 변경된다.") {
                    val account = AccountBuilder(status = AccountStatus.LOGOUT).build()
                    val accountEntity =
                        accountJpaRepository.save(
                            AccountEntityBuilder(
                                username = account.username,
                                status = account.status,
                            ).build(),
                        )

                    accountManager.login(account)

                    accountJpaRepository.findByIdOrNull(accountEntity.id)!!.status shouldBe AccountStatus.ENABLED
                }
            }
        },
    )
