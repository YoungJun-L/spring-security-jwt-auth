package com.youngjun.admin.domain.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface AdminUserRepository : JpaRepository<AdminUser, Long> {
    @Query("SELECT user FROM AdminUser user WHERE user.id <= :nextId ORDER BY user.id DESC LIMIT :limit")
    fun findAllWithCursor(
        limit: Int,
        nextId: Long,
    ): List<AdminUser>

    fun countByStatus(status: UserStatus): Long
}
