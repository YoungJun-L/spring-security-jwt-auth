package com.youngjun.auth.support

import io.kotest.core.annotation.Tags

@Tags("security")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class SecurityTest
