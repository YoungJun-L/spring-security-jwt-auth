package com.youngjun.auth.core.api.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.intercept.AuthorizationFilter
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@EnableWebSecurity
@Configuration
class SecurityConfiguration(
    private val authenticationSuccessHandler: AuthenticationSuccessHandler,
    private val authenticationEntryPoint: AuthenticationEntryPoint,
    private val jwtAuthenticationProvider: JwtAuthenticationProvider,
    private val userDetailsService: UserDetailsService,
    private val passwordEncoder: PasswordEncoder,
    private val bearerTokenResolver: BearerTokenResolver,
    private val authDetailsExchangeFilter: AuthDetailsExchangeFilter,
    private val objectMapper: ObjectMapper,
) {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .authorizeHttpRequests {
                it
                    .requestMatchers(*NotFilterRequestMatcher.matchers())
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }.addFilterAt(
                authenticationProcessingFilter(),
                UsernamePasswordAuthenticationFilter::class.java,
            ).addFilterAfter(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter::class.java)
            .addFilterAfter(authDetailsExchangeFilter, AuthorizationFilter::class.java)
            .exceptionHandling { it.authenticationEntryPoint(authenticationEntryPoint) }
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
    fun authenticationProcessingFilter(): AbstractAuthenticationProcessingFilter =
        RequestBodyUsernamePasswordAuthenticationFilter(
            authenticationManager(),
            objectMapper,
            authenticationSuccessHandler,
            authenticationFailureHandler(),
        )

    @Bean
    fun jwtAuthenticationFilter(): JwtAuthenticationFilter =
        JwtAuthenticationFilter(
            bearerTokenResolver,
            authenticationManager(),
            authenticationFailureHandler(),
        )

    @Bean
    fun authenticationManager(): AuthenticationManager {
        val daoAuthenticationProvider = DaoAuthenticationProvider(passwordEncoder)
        daoAuthenticationProvider.setUserDetailsService(userDetailsService)
        return ProviderManager(jwtAuthenticationProvider, daoAuthenticationProvider)
    }

    @Bean
    fun authenticationFailureHandler(): AuthenticationFailureHandler = AuthenticationEntryPointFailureHandler(authenticationEntryPoint)
}
