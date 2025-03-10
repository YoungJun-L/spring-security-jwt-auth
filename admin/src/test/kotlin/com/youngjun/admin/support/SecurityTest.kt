package com.youngjun.admin.support

import com.youngjun.tests.ContextTest
import io.kotest.core.annotation.Tags
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter

@Tags("security")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SecurityTest

@Tag("securityContext")
@ExtendWith(RestDocumentationExtension::class)
@ContextTest
abstract class SecurityContextTest {
    @BeforeEach
    fun setUp(
        webApplicationContext: WebApplicationContext,
        restDocumentation: RestDocumentationContextProvider,
    ) {
        setMockMvc(webApplicationContext, restDocumentation)
    }

    private fun setMockMvc(
        webApplicationContext: WebApplicationContext,
        restDocumentation: RestDocumentationContextProvider,
    ) {
        RestAssuredMockMvc.mockMvc(
            MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .addFilter<DefaultMockMvcBuilder>(CharacterEncodingFilter(Charsets.UTF_8.name(), true))
                .apply<DefaultMockMvcBuilder>(mockMvcConfigurer(restDocumentation))
                .apply<DefaultMockMvcBuilder>(springSecurity())
                .build(),
        )
    }

    private fun mockMvcConfigurer(restDocumentation: RestDocumentationContextProvider) =
        MockMvcRestDocumentation
            .documentationConfiguration(restDocumentation)
            .operationPreprocessors()
            .withRequestDefaults(
                Preprocessors
                    .modifyUris()
                    .scheme("http")
                    .host("dev.youngjun.com")
                    .removePort(),
                Preprocessors.prettyPrint(),
            ).withResponseDefaults(Preprocessors.prettyPrint())
}

@RestController
class TestController {
    @GetMapping("/test")
    fun test() {
    }
}
