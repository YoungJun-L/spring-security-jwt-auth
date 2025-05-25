package com.youngjun.admin.api.controller.v1

import com.youngjun.admin.api.controller.v1.response.TemplateResponse
import com.youngjun.admin.api.controller.v1.response.TemplatesResponse
import com.youngjun.admin.application.AdminTemplateService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminTemplateController(
    private val adminTemplateService: AdminTemplateService,
) {
    @GetMapping("/templates")
    fun readTemplates(): ResponseEntity<TemplatesResponse> {
        val templates = adminTemplateService.findAll()
        return ResponseEntity.ok(TemplatesResponse(templates.map { TemplateResponse.from(it) }))
    }
}
