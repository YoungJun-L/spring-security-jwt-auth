package com.youngjun.admin.domain.support

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter
class StringSetConverter : AttributeConverter<Set<String>, String> {
    override fun convertToDatabaseColumn(attribute: Set<String>): String = attribute.joinToString(DELIMITER)

    override fun convertToEntityAttribute(dbData: String): Set<String> =
        dbData
            .split(DELIMITER)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .toSet()

    companion object {
        private const val DELIMITER = ","
    }
}
