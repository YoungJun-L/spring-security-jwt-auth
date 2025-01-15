package com.youngjun.core.api.controller.v1

import com.youngjun.core.api.application.SampleService
import com.youngjun.core.api.controller.v1.response.SampleResponse
import com.youngjun.core.api.support.response.ApiResponse
import com.youngjun.core.domain.User
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class SampleController(
    private val sampleService: SampleService,
) {
    @GetMapping("/sample")
    fun read(user: User): ApiResponse<SampleResponse> {
        val sample = sampleService.read(user)
        return ApiResponse.success(SampleResponse.from(sample))
    }
}
