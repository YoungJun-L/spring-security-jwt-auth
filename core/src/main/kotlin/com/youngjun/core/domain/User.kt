package com.youngjun.core.domain

data class User(
    val id: Long,
    val details: Map<String, Any> = emptyMap(),
)
