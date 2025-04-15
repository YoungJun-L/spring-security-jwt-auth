package com.youngjun.admin.api.controller.v1.response

import com.youngjun.admin.domain.user.DailyUserStats

data class DailyUserStatsListResponse(
    val dailyUserStats: List<DailyUserStatsResponse>,
) {
    companion object {
        fun of(dailyUserStats: List<DailyUserStats>): DailyUserStatsListResponse =
            DailyUserStatsListResponse(
                dailyUserStats = dailyUserStats.map { DailyUserStatsResponse.from(it) },
            )
    }
}
