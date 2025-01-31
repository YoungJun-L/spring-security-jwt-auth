package com.youngjun.auth.infra.db

import com.youngjun.auth.domain.account.AccountEntity
import org.springframework.data.jpa.repository.JpaRepository

interface AccountJpaRepository : JpaRepository<AccountEntity, Long> {
    fun findByUsername(username: String): AccountEntity?

    fun existsByUsername(username: String): Boolean
}
