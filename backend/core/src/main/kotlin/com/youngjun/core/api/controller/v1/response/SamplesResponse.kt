package com.youngjun.core.api.controller.v1.response

import com.youngjun.core.domain.Sample

data class SamplesResponse(
    val userId: Long,
    val samples: List<SampleResponse>,
) {
    companion object {
        fun of(
            userId: Long,
            samples: List<Sample>,
        ): SamplesResponse = SamplesResponse(userId, samples.map { SampleResponse.from(it) })
    }
}
