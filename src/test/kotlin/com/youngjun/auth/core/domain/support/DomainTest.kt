package com.youngjun.auth.core.domain.support

import io.kotest.core.annotation.Tags
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@Tags("domain")
@ActiveProfiles("test")
@SpringBootTest
@EnableAutoConfiguration(exclude = [SecurityAutoConfiguration::class])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class DomainTest
