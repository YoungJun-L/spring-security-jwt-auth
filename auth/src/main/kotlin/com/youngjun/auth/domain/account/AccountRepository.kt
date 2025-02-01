package com.youngjun.auth.domain.account

import com.youngjun.auth.infra.db.AccountJpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class AccountRepository(
    private val accountJpaRepository: AccountJpaRepository,
) {
    fun save(account: Account): Account = accountJpaRepository.save(account)

    fun write(newAccount: NewAccount): Account = accountJpaRepository.save(Account(newAccount.username, newAccount.password))

    fun read(username: String): Account? = accountJpaRepository.findByUsername(username)

    fun read(id: Long): Account? = accountJpaRepository.findByIdOrNull(id)

    fun existsByUsername(username: String): Boolean = accountJpaRepository.existsByUsername(username)
}
