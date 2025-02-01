package com.youngjun.core.infra.db

import com.youngjun.core.domain.Sample
import org.springframework.data.jpa.repository.JpaRepository

interface SampleJpaRepository : JpaRepository<Sample, Long> {
    fun findByIdAndUserId(
        id: Long,
        userId: Long,
    ): Sample?

    fun findAllByUserId(userId: Long): List<Sample>
}
