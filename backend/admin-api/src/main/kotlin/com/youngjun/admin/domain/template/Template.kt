package com.youngjun.admin.domain.template

import com.youngjun.admin.domain.support.BaseEntity
import com.youngjun.admin.domain.support.StringSetConverter
import jakarta.persistence.Column
import jakarta.persistence.Convert
import jakarta.persistence.Embedded
import jakarta.persistence.Entity

@Entity
class Template(
    templateMeta: TemplateMeta,
    subject: String,
    body: String,
    variableNames: Set<String>,
) : BaseEntity() {
    @Embedded
    var templateMeta = templateMeta
        protected set

    @Column
    var subject = subject
        protected set

    @Column(columnDefinition = "text")
    var body = body
        protected set

    @Convert(converter = StringSetConverter::class)
    @Column
    var variableNames = variableNames
        protected set
}
