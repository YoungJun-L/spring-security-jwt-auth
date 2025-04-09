package com.youngjun.core.api.controller.v1

import com.youngjun.core.api.controller.v1.response.SampleResponse
import com.youngjun.core.api.controller.v1.response.SamplesResponse
import com.youngjun.core.application.SampleService
import com.youngjun.core.domain.AnyUser
import com.youngjun.core.domain.User
import com.youngjun.core.support.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController(
    private val sampleService: SampleService,
) {
    @GetMapping("/samples/{sampleId}")
    fun readSample(
        user: User,
        @PathVariable sampleId: Long,
    ): ApiResponse<SampleResponse> {
        val sample = sampleService.readSample(user, sampleId)
        return ApiResponse.success(SampleResponse.from(sample))
    }

    @GetMapping("/samples")
    fun readSamples(anyUser: AnyUser): ApiResponse<SamplesResponse> {
        val samples = sampleService.readSamples(anyUser.toUser())
        return ApiResponse.success(SamplesResponse.of(anyUser.id, samples))
    }
}
