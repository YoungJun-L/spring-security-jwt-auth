package com.youngjun.auth.support

import io.kotest.core.annotation.Tags

@Tags("domain")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DomainTest

@DomainTest
@ContextTest
annotation class DomainContextTest
