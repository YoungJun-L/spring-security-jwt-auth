package com.youngjun.admin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@ConfigurationPropertiesScan
@SpringBootApplication
class AdminTestApplication

fun main(args: Array<String>) {
    runApplication<AdminTestApplication>(*args)
}
