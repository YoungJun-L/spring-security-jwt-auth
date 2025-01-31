package com.youngjun.core.application

import com.youngjun.core.domain.Sample
import com.youngjun.core.domain.User
import com.youngjun.core.support.error.CoreException
import com.youngjun.core.support.error.ErrorType.SAMPLE_NOT_FOUND_ERROR
import com.youngjun.storage.db.core.SampleRepository
import org.springframework.stereotype.Service

@Service
class SampleService(
    private val sampleRepository: SampleRepository,
) {
    fun read(user: User): Sample = sampleRepository.find(user) ?: throw CoreException(SAMPLE_NOT_FOUND_ERROR)
}
