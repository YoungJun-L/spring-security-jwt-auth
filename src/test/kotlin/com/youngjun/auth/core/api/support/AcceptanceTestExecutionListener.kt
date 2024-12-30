package com.youngjun.auth.core.api.support

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration
import org.springframework.test.context.TestContext
import org.springframework.test.context.support.AbstractTestExecutionListener

object AcceptanceTestExecutionListener : AbstractTestExecutionListener() {
    override fun beforeTestMethod(testContext: TestContext) {
        setUpRestDocs(testContext)
    }

    private fun setUpRestDocs(testContext: TestContext) {
        val serverPort =
            testContext.applicationContext.environment.getProperty("local.server.port", Int::class.java)
                ?: throw IllegalStateException("LocalServerPort cannot be null")
        val restDocumentation = testContext.applicationContext.getBean(RestDocumentationContextProvider::class.java)
        RestAssured.requestSpecification =
            RequestSpecBuilder().addFilter(documentationConfiguration(restDocumentation)).setPort(serverPort).build()
    }

    override fun afterTestMethod(testContext: TestContext) {
        val jdbcTemplate = testContext.applicationContext.getBean(JdbcTemplate::class.java)
        val truncateQueries = getTruncateQueries(jdbcTemplate)
        truncateTables(jdbcTemplate, truncateQueries)
        RestAssured.requestSpecification = null
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
