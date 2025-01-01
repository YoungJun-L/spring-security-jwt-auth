package com.youngjun.auth.core.api.support

import io.restassured.specification.RequestSpecification
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName
import org.springframework.restdocs.restassured.RestAssuredRestDocumentation
import org.springframework.restdocs.snippet.Snippet

fun RequestSpecification.document(
    identifier: String,
    vararg snippets: Snippet,
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

infix fun String.type(type: JsonFieldType): FieldDescriptor = fieldWithPath(this).type(type)

infix fun String.description(description: String): ParameterDescriptor = parameterWithName(this).description(description)

infix fun ParameterDescriptor.optional(isOptional: Boolean): ParameterDescriptor = if (isOptional) this.optional() else this

infix fun ParameterDescriptor.ignored(isIgnored: Boolean): ParameterDescriptor = if (isIgnored) this.ignored() else this

infix fun FieldDescriptor.description(description: String): FieldDescriptor = this.description(description)

infix fun FieldDescriptor.optional(isOptional: Boolean): FieldDescriptor = if (isOptional) this.optional() else this

infix fun FieldDescriptor.ignored(isIgnored: Boolean): FieldDescriptor = if (isIgnored) this.ignored() else this
