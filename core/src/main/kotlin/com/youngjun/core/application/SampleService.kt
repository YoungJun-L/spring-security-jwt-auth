package com.youngjun.core.application

import com.youngjun.core.domain.Sample
import com.youngjun.core.domain.SampleRepository
import com.youngjun.core.domain.User
import com.youngjun.core.support.error.CoreException
import com.youngjun.core.support.error.ErrorType
import org.springframework.stereotype.Service

@Service
class SampleService(
    private val sampleRepository: SampleRepository,
) {
    fun readSample(
        user: User,
        sampleId: Long,
    ): Sample = sampleRepository.findBy(user, sampleId) ?: throw CoreException(ErrorType.SAMPLE_NOT_FOUND_ERROR)

    fun readSamples(user: User): List<Sample> = sampleRepository.findAllBy(user)
}
