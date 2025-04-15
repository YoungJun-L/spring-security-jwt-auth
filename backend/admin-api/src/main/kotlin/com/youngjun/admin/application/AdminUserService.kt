package com.youngjun.admin.application

import com.youngjun.admin.domain.user.AdminUser
import com.youngjun.admin.domain.user.AdminUserCount
import com.youngjun.admin.domain.user.AdminUserRepository
import com.youngjun.admin.domain.user.DailyUserStats
import com.youngjun.admin.domain.user.DailyUserStatsRepository
import com.youngjun.admin.domain.user.UserStatus
import org.springframework.stereotype.Service

@Service
class AdminUserService(
    private val adminUserRepository: AdminUserRepository,
    private val dailyUserStatsRepository: DailyUserStatsRepository,
) {
    fun findAllPagedByCreatedAtDesc(
        pageSize: Int,
        nextId: Long,
    ): Pair<List<AdminUser>, Long> {
        val users = adminUserRepository.findAllWithCursor(pageSize + 1, nextId)
        return users.dropLast(1) to
            if (users.size > pageSize) {
                users.last().id
            } else {
                -1L
            }
    }

    fun count(): AdminUserCount {
        val totalUserCount = adminUserRepository.count()
        val activeUserCount = adminUserRepository.countByStatus(UserStatus.ENABLED)
        return AdminUserCount(
            totalUser = totalUserCount,
            activeUser = activeUserCount,
            inactiveUser = totalUserCount - activeUserCount,
        )
    }

    fun getDailyUserStats(): List<DailyUserStats> = dailyUserStatsRepository.findAll()
}
