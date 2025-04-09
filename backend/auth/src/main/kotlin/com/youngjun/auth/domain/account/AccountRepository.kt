package com.youngjun.auth.domain.account

import org.springframework.data.jpa.repository.JpaRepository

interface AccountRepository : JpaRepository<Account, Long> {
    fun findByEmailAddress(emailAddress: EmailAddress): Account?

    fun existsByEmailAddress(emailAddress: EmailAddress): Boolean
}
