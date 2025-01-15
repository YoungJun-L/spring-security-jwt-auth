package com.youngjun.auth.storage.db.core.account

import org.springframework.data.jpa.repository.JpaRepository

interface AccountJpaRepository : JpaRepository<AccountEntity, Long> {
    fun findByUsername(username: String): AccountEntity?

    fun existsByUsername(username: String): Boolean
}
