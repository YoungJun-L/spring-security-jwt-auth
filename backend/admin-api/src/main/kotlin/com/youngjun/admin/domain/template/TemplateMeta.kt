package com.youngjun.admin.domain.template

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class TemplateMeta(
    @Column
    val type: TemplateType,
    @Column
    val version: Int,
)
