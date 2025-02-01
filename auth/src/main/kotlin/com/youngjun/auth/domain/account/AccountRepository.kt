package com.youngjun.auth.domain.account

import com.youngjun.auth.infra.db.AccountJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class AccountRepository(
    private val accountJpaRepository: AccountJpaRepository,
) {
    fun save(account: Account): Account = accountJpaRepository.save(account)

    fun findBy(username: String): Account? = accountJpaRepository.findByUsername(username)

    fun findBy(id: Long): Account? = accountJpaRepository.findByIdOrNull(id)

    fun existsBy(username: String): Boolean = accountJpaRepository.existsByUsername(username)
}
