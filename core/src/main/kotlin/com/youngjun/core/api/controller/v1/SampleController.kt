package com.youngjun.core.api.controller.v1

import com.youngjun.core.api.controller.v1.response.SampleResponse
import com.youngjun.core.application.SampleService
import com.youngjun.core.domain.User
import com.youngjun.core.support.response.ApiResponse
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
