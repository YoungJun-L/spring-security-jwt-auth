package com.youngjun.admin.domain.template

import org.springframework.data.jpa.repository.JpaRepository

interface TemplateRepository : JpaRepository<Template, Long> {
    fun findByTemplateMeta(templateMeta: TemplateMeta): Template?
}
