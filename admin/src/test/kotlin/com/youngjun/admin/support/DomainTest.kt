package com.youngjun.admin.support

import com.youngjun.tests.ContextTest
import io.kotest.core.annotation.Tags

@Tags("domain")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DomainTest

@DomainTest
@ContextTest
annotation class DomainContextTest
