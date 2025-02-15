package com.youngjun.auth.infra.db

import com.youngjun.auth.domain.account.Account
import com.youngjun.auth.domain.account.EmailAddress
import org.springframework.data.jpa.repository.JpaRepository

interface AccountJpaRepository : JpaRepository<Account, Long> {
    fun findByEmailAddress(emailAddress: EmailAddress): Account?

    fun existsByEmailAddress(emailAddress: EmailAddress): Boolean
}
