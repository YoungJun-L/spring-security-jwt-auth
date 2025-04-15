package com.youngjun.admin.domain.user

import com.youngjun.admin.domain.support.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime

@Table(name = "daily_user_stats")
@Entity
class DailyUserStats(
    statDate: LocalDateTime,
    totalUserCount: Long,
    activeUserCount: Long,
) : BaseEntity() {
    @Column
    var statDate: LocalDateTime = statDate
        protected set

    @Column
    var totalUserCount: Long = totalUserCount
        protected set

    @Column
    var activeUserCount: Long = activeUserCount
        protected set
}
