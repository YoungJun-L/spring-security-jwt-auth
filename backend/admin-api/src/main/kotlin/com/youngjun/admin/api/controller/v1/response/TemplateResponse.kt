package com.youngjun.admin.api.controller.v1.response

import com.youngjun.admin.domain.template.Template
import com.youngjun.admin.domain.template.TemplateType

data class TemplateResponse(
    val templateType: TemplateType,
    val version: Int,
    val subject: String,
    val body: String,
    val variableNames: Set<String>,
) {
    companion object {
        fun from(template: Template) =
            TemplateResponse(
                templateType = template.templateMeta.type,
                version = template.templateMeta.version,
                subject = template.subject,
                body = template.body,
                variableNames = template.variableNames,
            )
    }
}
