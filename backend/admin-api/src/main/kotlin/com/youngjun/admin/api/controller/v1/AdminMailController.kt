package com.youngjun.admin.api.controller.v1

import com.youngjun.admin.api.controller.v1.request.SendMailRequest
import com.youngjun.admin.application.AdminMailService
import com.youngjun.admin.application.AdminTemplateService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AdminMailController(
    private val adminMailService: AdminMailService,
    private val adminTemplateService: AdminTemplateService,
) {
    @PostMapping("/emails/send")
    fun sendMail(
        @RequestBody request: SendMailRequest,
    ): ResponseEntity<Unit> {
        val templateId = adminTemplateService.validateResolvable(request.template, request.recipients)
        adminMailService.submit(templateId, request.recipients)
        return ResponseEntity.accepted().build()
    }
}
