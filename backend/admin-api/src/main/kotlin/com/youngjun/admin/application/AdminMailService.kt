package com.youngjun.admin.application

import com.youngjun.admin.domain.mail.MailMessageInfo
import com.youngjun.admin.domain.mail.MailTask
import com.youngjun.admin.domain.mail.MailTaskRepository
import com.youngjun.admin.domain.template.MailContent
import com.youngjun.admin.domain.template.TemplatedRecipient
import com.youngjun.admin.infra.mail.AdminMailSender
import jakarta.transaction.Transactional
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service

@Service
class AdminMailService(
    private val mailTaskRepository: MailTaskRepository,
    private val adminMailSender: AdminMailSender,
) {
    fun submit(
        templateId: Long,
        templatedRecipient: List<TemplatedRecipient>,
    ) {
        mailTaskRepository.saveAll(
            templatedRecipient.map { MailTask(mailMessageInfo = MailMessageInfo(templateId, it.email, it.variables)) },
        )
    }

    @Async
    fun send(
        task: MailTask,
        content: MailContent,
    ) {
        task.markAsProcessing()
        try {
            adminMailSender.send(task.mailMessageInfo.recipient, content)
            task.markAsSent()
        } catch (ex: Exception) {
            task.markAsFailed()
        }
        mailTaskRepository.save(task)
    }

    @Transactional
    fun findAllPending(): List<MailTask> = mailTaskRepository.findAllPending().onEach { it.markAsProcessing() }

    @Transactional
    fun findAllRetryable(): List<MailTask> = mailTaskRepository.findRetryableFailedTasks().onEach { it.markAsProcessing() }
}
