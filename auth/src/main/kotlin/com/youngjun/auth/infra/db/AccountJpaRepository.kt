package com.youngjun.auth.infra.db

import com.youngjun.auth.domain.account.Account
import org.springframework.data.jpa.repository.JpaRepository

interface AccountJpaRepository : JpaRepository<Account, Long> {
    fun findByUsername(username: String): Account?

    fun existsByUsername(username: String): Boolean
}
