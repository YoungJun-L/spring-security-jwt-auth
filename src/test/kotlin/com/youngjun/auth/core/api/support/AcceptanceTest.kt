package com.youngjun.auth.core.api.support

import com.youngjun.auth.AuthApplication
import io.kotest.core.annotation.Tags
import io.restassured.specification.RequestSpecification
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor
import org.springframework.test.context.TestExecutionListeners

@Tags("acceptance")
@ActiveProfiles("test")
@TestExecutionListeners(
    value = [AcceptanceTestExecutionListener::class],
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@AutoConfigureRestDocs
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [AuthApplication::class],
    properties = ["spring.profiles.active=test"],
)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class AcceptanceTest

fun RequestSpecification.document(
    identifier: String?,
    vararg snippets: Snippet?,
): RequestSpecification =
    filter(
        RestAssuredRestDocumentation.document(identifier, requestPreprocessor(), responsePreprocessor(), *snippets),
    )

private fun requestPreprocessor(): OperationRequestPreprocessor =
    Preprocessors.preprocessRequest(
        Preprocessors
            .modifyUris()
            .scheme("http")
            .host("dev.youngjun.com")
            .removePort(),
        Preprocessors.prettyPrint(),
    )

private fun responsePreprocessor(): OperationResponsePreprocessor = Preprocessors.preprocessResponse(Preprocessors.prettyPrint())
