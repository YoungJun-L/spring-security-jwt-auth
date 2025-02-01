package com.youngjun.core.domain

import com.youngjun.core.infra.db.SampleJpaRepository
import org.springframework.stereotype.Repository

@Repository
class SampleRepository(
    private val sampleJpaRepository: SampleJpaRepository,
) {
    fun findBy(
        user: User,
        sampleId: Long,
    ): Sample? = sampleJpaRepository.findByIdAndUserId(sampleId, user.id)

    fun findAllBy(user: User): List<Sample> = sampleJpaRepository.findAllByUserId(user.id)
}
