package com.youngjun.auth.support

import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation.parameterWithName

infix fun String.type(type: JsonFieldType): FieldDescriptor = fieldWithPath(this).type(type)

infix fun String.description(description: String): ParameterDescriptor = parameterWithName(this).description(description)

infix fun ParameterDescriptor.optional(isOptional: Boolean): ParameterDescriptor = if (isOptional) optional() else this

infix fun ParameterDescriptor.ignored(isIgnored: Boolean): ParameterDescriptor = if (isIgnored) ignored() else this

infix fun FieldDescriptor.description(description: String): FieldDescriptor = description(description)

infix fun FieldDescriptor.optional(isOptional: Boolean): FieldDescriptor = if (isOptional) optional() else this

infix fun FieldDescriptor.ignored(isIgnored: Boolean): FieldDescriptor = if (isIgnored) ignored() else this
