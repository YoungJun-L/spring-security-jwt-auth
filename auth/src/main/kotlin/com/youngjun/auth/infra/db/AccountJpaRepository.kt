package com.youngjun.auth.infra.db

import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.domain.account.Email
import org.springframework.data.jpa.repository.JpaRepository

interface AccountJpaRepository : JpaRepository<Account, Long> {
    fun findByEmail(email: Email): Account?

    fun existsByEmail(email: Email): Boolean
}
