package com.youngjun.core.support

import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName

infix fun String.type(type: JsonFieldType): FieldDescriptor = fieldWithPath(this).type(type)

infix fun String.description(description: String): ParameterDescriptor = parameterWithName(this).description(description)

infix fun ParameterDescriptor.optional(isOptional: Boolean): ParameterDescriptor = if (isOptional) this.optional() else this

infix fun ParameterDescriptor.ignored(isIgnored: Boolean): ParameterDescriptor = if (isIgnored) this.ignored() else this

infix fun FieldDescriptor.description(description: String): FieldDescriptor = this.description(description)

infix fun FieldDescriptor.optional(isOptional: Boolean): FieldDescriptor = if (isOptional) this.optional() else this

infix fun FieldDescriptor.ignored(isIgnored: Boolean): FieldDescriptor = if (isIgnored) this.ignored() else this
