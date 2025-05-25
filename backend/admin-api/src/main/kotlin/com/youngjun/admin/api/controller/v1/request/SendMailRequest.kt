package com.youngjun.admin.api.controller.v1.request

import com.youngjun.admin.domain.template.TemplateMeta
import com.youngjun.admin.domain.template.TemplatedRecipient

data class SendMailRequest(
    val template: TemplateMeta,
    val recipients: List<TemplatedRecipient>,
)
