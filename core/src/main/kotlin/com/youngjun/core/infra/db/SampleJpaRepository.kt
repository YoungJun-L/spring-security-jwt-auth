package com.youngjun.core.infra.db

import com.youngjun.core.domain.Sample
import org.springframework.data.jpa.repository.JpaRepository

interface SampleJpaRepository : JpaRepository<Sample, Long> {
    fun findByUserId(userId: Long): Sample?
}
