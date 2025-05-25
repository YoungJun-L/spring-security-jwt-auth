package com.youngjun.admin.domain.template

data class TemplateBuilder(
    val templateType: TemplateType = TemplateType("welcome"),
    val version: Int = 1,
    val templateMeta: TemplateMeta = TemplateMeta(templateType, version),
    val subject: String = "${'$'}{name}님 환영합니다.",
    val body: String =
        """
        <!DOCTYPE html>
        <html lang="ko">
        <head>
            <meta content="text/html; charset=utf-8" http-equiv="Content-Type"/>
        </head>
        <body>
            <div>${'$'}{name}님 환영합니다.</div>
        </body>
        </html>
        """.trimIndent(),
    val variableNames: Set<String> = setOf("name"),
) {
    fun build(): Template = Template(templateMeta, subject, body, variableNames)
}
