package com.youngjun.admin.infra.scheduler

import com.youngjun.admin.application.AdminMailService
import com.youngjun.admin.application.AdminTemplateService
import org.springframework.scheduling.annotation.Async
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class AdminMailTaskProcessor(
    private val adminMailService: AdminMailService,
    private val adminTemplateService: AdminTemplateService,
) {
    @Async
    @Scheduled(fixedDelay = 1_000)
    fun processPendingTasks() {
        val tasks = adminMailService.findAllPending()
        val contents = adminTemplateService.resolve(tasks.map { it.mailMessageInfo })
        tasks
            .zip(contents)
            .map { (task, content) -> adminMailService.send(task, content) }
    }

    @Async
    @Scheduled(fixedDelay = 1_000)
    fun processFailedTasks() {
        val tasks = adminMailService.findAllRetryable()
        val contents = adminTemplateService.resolve(tasks.map { it.mailMessageInfo })
        tasks
            .zip(contents)
            .map { (task, content) -> adminMailService.send(task, content) }
    }
}
