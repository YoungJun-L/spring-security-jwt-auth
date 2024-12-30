package com.youngjun.auth.core.api.support

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.names.TestName
import io.kotest.core.spec.style.scopes.FunSpecContainerScope
import io.kotest.core.spec.style.scopes.FunSpecRootScope
import io.kotest.core.spec.style.scopes.addContainer
import io.kotest.core.test.TestScope
import io.kotest.extensions.spring.testContextManager
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.util.StreamUtils
import java.nio.charset.Charset

fun FunSpecRootScope.context(
    name: String,
    setupFiles: List<String>,
    test: suspend FunSpecContainerScope.() -> Unit,
) {
    this.addContainer(TestName("context ", name, false), false, null) {
        batchInsert(convertToInsertQueries(setupFiles))
        FunSpecContainerScope(this).test()
    }
}

suspend fun FunSpecContainerScope.test(
    name: String,
    setupFiles: List<String>,
    test: suspend TestScope.() -> Unit,
) {
    this.test(name) {
        batchInsert(convertToInsertQueries(setupFiles))
        test()
    }
}

private suspend fun convertToInsertQueries(setupFiles: List<String>) = setupFiles.map { deserialize(it) }.flatMap { toInsertQueries(it) }

private suspend fun deserialize(file: String): Map<String, List<Map<String, String>>> {
    val objectMapper = testContextManager().testContext.applicationContext.getBean(ObjectMapper::class.java)
    return objectMapper.readValue(
        StreamUtils.copyToString(ClassPathResource(file).inputStream, Charset.defaultCharset()),
        Map::class.java,
    ) as Map<String, List<Map<String, String>>>
}

private fun toInsertQueries(data: Map<String, List<Map<String, Any>>>): List<String> =
    data.flatMap { (tableName, rows) ->
        rows.map { row ->
            val columns = row.keys.joinToString(", ")
            val values =
                row.values.joinToString(", ") { value: Any? ->
                    formatValue(value)
                }
            "INSERT INTO $tableName ($columns) VALUES ($values);"
        }
    }

private fun formatValue(value: Any?): String =
    when {
        value == null -> "NULL"
        value is String && value.lowercase() == "now()" -> "now()"
        else -> "'$value'"
    }

private suspend fun batchInsert(queries: List<String>) {
    val jdbcTemplate = testContextManager().testContext.applicationContext.getBean(JdbcTemplate::class.java)
    jdbcTemplate.batchUpdate(*queries.toTypedArray())
}
