package com.youngjun.auth.core.domain.token

fun interface TimeHolder {
    fun now(): Long

    companion object {
        val Default = TimeHolder { System.currentTimeMillis() }
    }
}
