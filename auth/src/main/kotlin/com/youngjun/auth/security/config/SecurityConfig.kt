package com.youngjun.auth.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.youngjun.auth.application.AccountService
import com.youngjun.auth.application.TokenService
import com.youngjun.auth.security.filter.JsonLoginAuthenticationFilter
import com.youngjun.auth.security.filter.JwtAuthenticationFilter
import com.youngjun.auth.security.filter.LogoutFilter
import com.youngjun.auth.security.filter.UserCookieExchangeFilter
import com.youngjun.auth.security.handler.ApiAuthenticationEntryPoint
import com.youngjun.auth.security.handler.JsonResponseWriter
import com.youngjun.auth.security.handler.LoginSuccessHandler
import com.youngjun.auth.security.provider.JwtAuthenticationProvider
import com.youngjun.auth.security.token.BearerTokenResolver
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.AuthorizationFilter
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@EnableWebSecurity
@Configuration
private class SecurityConfig(
    private val tokenService: TokenService,
    private val accountService: AccountService,
    private val passwordEncoder: PasswordEncoder,
    private val objectMapper: ObjectMapper,
    private val jsonResponseWriter: JsonResponseWriter,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .authorizeHttpRequests {
                it
                    .requestMatchers(*NotFilterRequestMatcher.matchers())
                    .permitAll()
                    .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/samples"))
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }.addFilterAt(
                JsonLoginAuthenticationFilter(
                    authenticationManager(),
                    objectMapper,
                    LoginSuccessHandler(accountService, tokenService, jsonResponseWriter),
                    authenticationFailureHandler(),
                ),
                UsernamePasswordAuthenticationFilter::class.java,
            ).addFilterAfter(
                JwtAuthenticationFilter(
                    BearerTokenResolver,
                    authenticationManager(),
                    authenticationFailureHandler(),
                ),
                UsernamePasswordAuthenticationFilter::class.java,
            ).addFilterAfter(
                LogoutFilter(accountService, jsonResponseWriter),
                JwtAuthenticationFilter::class.java,
            ).addFilterAfter(
                UserCookieExchangeFilter,
                AuthorizationFilter::class.java,
            ).exceptionHandling { it.authenticationEntryPoint(authenticationEntryPoint()) }
            .authenticationManager(authenticationManager())
            .headers { it.disable() }
            .formLogin { it.disable() }
            .requestCache { it.disable() }
            .logout { it.disable() }
            .cors { it.disable() }
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .build()

    @Bean
    fun authenticationManager(): AuthenticationManager =
        ProviderManager(
            JwtAuthenticationProvider(tokenService),
            DaoAuthenticationProvider(passwordEncoder).apply {
                setUserDetailsService(accountService)
            },
        )

    @Bean
    fun authenticationFailureHandler(): AuthenticationFailureHandler = AuthenticationEntryPointFailureHandler(authenticationEntryPoint())

    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint = ApiAuthenticationEntryPoint(jsonResponseWriter)
}
