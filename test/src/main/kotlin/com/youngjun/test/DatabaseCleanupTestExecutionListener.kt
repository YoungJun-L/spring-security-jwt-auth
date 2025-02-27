package com.youngjun.test

import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.test.context.TestContext
import org.springframework.test.context.support.AbstractTestExecutionListener

object DatabaseCleanupTestExecutionListener : AbstractTestExecutionListener() {
    override fun afterTestMethod(testContext: TestContext) {
        val jdbcTemplate = testContext.applicationContext.getBean(JdbcTemplate::class.java)
        val truncateQueries = getTruncateQueries(jdbcTemplate)
        truncateTables(jdbcTemplate, truncateQueries)
    }

    private fun getTruncateQueries(jdbcTemplate: JdbcTemplate): List<String> =
        jdbcTemplate.queryForList(
            "SELECT Concat('TRUNCATE TABLE ', TABLE_NAME, ';') AS q FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC'",
            String::class.java,
        )

    private fun truncateTables(
        jdbcTemplate: JdbcTemplate,
        truncateQueries: List<String>,
    ) {
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 0")
        truncateQueries.forEach { query -> jdbcTemplate.execute(query) }
        jdbcTemplate.execute("SET FOREIGN_KEY_CHECKS = 1")
    }
}
