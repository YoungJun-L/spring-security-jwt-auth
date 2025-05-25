package com.youngjun.admin.domain.support

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Component
@Converter
class StringMapConverter(
    private val objectMapper: ObjectMapper,
) : AttributeConverter<Map<String, String>, String> {
    override fun convertToDatabaseColumn(attribute: Map<String, String>): String = objectMapper.writeValueAsString(attribute)

    override fun convertToEntityAttribute(dbData: String): Map<String, String> = objectMapper.readValue(dbData)
}
