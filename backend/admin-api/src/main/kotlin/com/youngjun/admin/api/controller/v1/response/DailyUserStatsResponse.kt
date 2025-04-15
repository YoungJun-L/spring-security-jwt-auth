package com.youngjun.admin.api.controller.v1.response

import com.youngjun.admin.domain.user.DailyUserStats
import java.time.LocalDate

data class DailyUserStatsResponse(
    val statDate: LocalDate,
    val activeUser: Long,
) {
    companion object {
        fun from(dailyUserStats: DailyUserStats): DailyUserStatsResponse =
            DailyUserStatsResponse(
                dailyUserStats.statDate.toLocalDate(),
                dailyUserStats.activeUserCount,
            )
    }
}
