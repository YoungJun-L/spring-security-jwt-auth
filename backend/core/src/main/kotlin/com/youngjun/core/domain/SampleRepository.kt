package com.youngjun.core.domain

import org.springframework.data.jpa.repository.JpaRepository

interface SampleRepository : JpaRepository<Sample, Long> {
    fun findByIdAndUserId(
        id: Long,
        userId: Long,
    ): Sample?

    fun findAllByUserId(userId: Long): List<Sample>
}
