package com.youngjun.core.domain

import com.youngjun.core.infra.db.SampleJpaRepository
import org.springframework.stereotype.Repository

@Repository
class SampleRepository(
    private val sampleJpaRepository: SampleJpaRepository,
) {
    fun find(user: User): Sample? = sampleJpaRepository.findByUserId(user.id)
}
