package com.youngjun.admin.application

import com.youngjun.admin.domain.mail.MailMessageInfo
import com.youngjun.admin.domain.template.MailContent
import com.youngjun.admin.domain.template.Template
import com.youngjun.admin.domain.template.TemplateMeta
import com.youngjun.admin.domain.template.TemplateRepository
import com.youngjun.admin.domain.template.TemplatedRecipient
import com.youngjun.admin.support.error.AdminException
import com.youngjun.admin.support.error.ErrorType.TEMPLATE_NOT_FOUND
import com.youngjun.admin.support.error.ErrorType.TEMPLATE_VARIABLES_NOT_MATCH
import org.springframework.stereotype.Service

@Service
class AdminTemplateService(
    private val templateRepository: TemplateRepository,
) {
    fun validateResolvable(
        templateMeta: TemplateMeta,
        templatedRecipient: List<TemplatedRecipient>,
    ): Long {
        val template = templateRepository.findByTemplateMeta(templateMeta) ?: throw AdminException(TEMPLATE_NOT_FOUND)
        if (templatedRecipient.any { it.variables.keys != template.variableNames }) {
            throw AdminException(TEMPLATE_VARIABLES_NOT_MATCH)
        }
        return template.id
    }

    fun resolve(messages: List<MailMessageInfo>): List<MailContent> {
        val templates = templateRepository.findAllById(messages.map { it.templateId }.toSet()).associateBy { it.id }
        return messages.map { message ->
            val template = templates[message.templateId] ?: throw AdminException(TEMPLATE_NOT_FOUND)
            val subject = render(template.subject, message.variables)
            val body = render(template.body, message.variables)
            MailContent(subject, body)
        }
    }

    private fun render(
        text: String,
        variables: Map<String, Any>,
    ): String =
        variables.entries.fold(text) { acc, (key, value) ->
            acc.replace("${'$'}{$key}", value.toString())
        }

    fun findAll(): List<Template> = templateRepository.findAll()
}
