package com.youngjun.auth.core.support

import com.ninjasquad.springmockk.MockkBean
import com.youngjun.auth.core.api.application.AccountService
import com.youngjun.auth.core.api.application.TokenService
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.context.WebApplicationContext

@Tag("securityContext")
@ActiveProfiles("test")
@ExtendWith(RestDocumentationExtension::class)
@ComponentScan("com.youngjun.auth.core.api.security")
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@WebMvcTest
abstract class SecurityContextTest {
    @MockkBean
    private lateinit var accountService: AccountService

    @MockkBean
    private lateinit var tokenService: TokenService

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
