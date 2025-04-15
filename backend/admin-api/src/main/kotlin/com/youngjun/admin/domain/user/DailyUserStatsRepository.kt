package com.youngjun.admin.domain.user

import org.springframework.data.jpa.repository.JpaRepository

interface DailyUserStatsRepository : JpaRepository<DailyUserStats, Long>
