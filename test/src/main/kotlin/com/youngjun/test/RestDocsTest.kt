package com.youngjun.test

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import org.springframework.web.filter.CharacterEncodingFilter
import org.springframework.web.method.support.HandlerMethodArgumentResolver

@Tag("restDocs")
@ExtendWith(RestDocumentationExtension::class)
abstract class RestDocsTest {
    private lateinit var restDocumentation: RestDocumentationContextProvider

    @BeforeEach
    fun setUp(restDocumentation: RestDocumentationContextProvider) {
        this.restDocumentation = restDocumentation
    }

    protected fun setMockMvc(
        controller: Any,
        vararg argumentResolvers: HandlerMethodArgumentResolver,
    ) {
        RestAssuredMockMvc.mockMvc(
            MockMvcBuilders
                .standaloneSetup(controller)
                .addFilter<StandaloneMockMvcBuilder>(CharacterEncodingFilter(Charsets.UTF_8.name(), true))
                .apply<StandaloneMockMvcBuilder>(mockMvcConfigurer())
                .setCustomArgumentResolvers(*argumentResolvers)
                .setMessageConverters(MappingJackson2HttpMessageConverter(objectMapper()))
                .build(),
        )
    }

    private fun mockMvcConfigurer() =
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

    private fun objectMapper() =
        jacksonObjectMapper()
            .findAndRegisterModules()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .disable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
}
