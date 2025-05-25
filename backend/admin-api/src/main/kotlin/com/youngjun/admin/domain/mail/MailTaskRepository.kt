package com.youngjun.admin.domain.mail

import jakarta.persistence.LockModeType.PESSIMISTIC_WRITE
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface MailTaskRepository : JpaRepository<MailTask, Long> {
    @Lock(PESSIMISTIC_WRITE)
    @Query("SELECT t FROM MailTask t WHERE t.status = PENDING ORDER BY t.id LIMIT 10")
    fun findAllPending(): List<MailTask>

    @Lock(PESSIMISTIC_WRITE)
    @Query(
        """
        SELECT t 
        FROM MailTask t 
        WHERE 
            t.status = FAILED 
            AND
            (t.retryInfo.nextRetryAt IS NULL OR t.retryInfo.nextRetryAt <= :now)
        ORDER BY t.id
        LIMIT 10
        """,
    )
    fun findRetryableFailedTasks(
        @Param("now") now: LocalDateTime = LocalDateTime.now(),
    ): List<MailTask>
}
