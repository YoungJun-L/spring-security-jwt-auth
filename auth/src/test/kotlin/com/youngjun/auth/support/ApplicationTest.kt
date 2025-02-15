package com.youngjun.auth.support

import io.kotest.core.annotation.Tags

@Tags("application")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationTest

@ApplicationTest
@ContextTest
annotation class ApplicationContextTest
