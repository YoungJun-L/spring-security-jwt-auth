package com.youngjun.storage.db.core

import com.youngjun.core.domain.Sample
import com.youngjun.core.domain.User
import org.springframework.stereotype.Repository

@Repository
class SampleRepository(
    private val sampleJpaRepository: SampleJpaRepository,
) {
    fun find(user: User): Sample? = sampleJpaRepository.findByUserId(user.id)?.toSample()
}
