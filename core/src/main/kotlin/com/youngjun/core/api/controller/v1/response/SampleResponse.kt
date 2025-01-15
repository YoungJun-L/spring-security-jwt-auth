package com.youngjun.core.api.controller.v1.response

import com.youngjun.core.domain.Sample

data class SampleResponse(
    val id: Long,
    val userId: Long,
    val data: String,
) {
    companion object {
        fun from(sample: Sample): SampleResponse = SampleResponse(sample.id, sample.userId, sample.data)
    }
}
