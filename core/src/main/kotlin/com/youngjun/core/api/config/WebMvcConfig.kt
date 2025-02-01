package com.youngjun.core.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
    private val userArgumentResolver: UserArgumentResolver,
    private val anyUserArgumentResolver: AnyUserArgumentResolver,
) : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers += arrayOf(anyUserArgumentResolver, userArgumentResolver)
    }
}
