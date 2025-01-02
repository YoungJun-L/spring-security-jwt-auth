package com.youngjun.auth.core.domain.support

import com.youngjun.auth.core.api.support.ApplicationTest
import io.kotest.core.annotation.Tags

@Tags("domain")
@ApplicationTest
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DomainTest
