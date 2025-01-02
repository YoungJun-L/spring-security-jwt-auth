package com.youngjun.auth.core.api.support

import io.kotest.core.annotation.Tags
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestConstructor

@Tags("application")
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = [SecurityAutoConfiguration::class, WebMvcAutoConfiguration::class])
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ApplicationTest
