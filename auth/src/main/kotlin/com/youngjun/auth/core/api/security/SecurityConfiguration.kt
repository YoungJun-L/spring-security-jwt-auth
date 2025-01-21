package com.youngjun.auth.core.api.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.youngjun.auth.core.api.application.AccountService
import com.youngjun.auth.core.api.application.TokenService
import com.youngjun.auth.core.domain.account.AccountReader
import com.youngjun.auth.core.domain.token.TokenParser
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
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

@EnableWebSecurity
@Configuration
class SecurityConfiguration(
    private val tokenService: TokenService,
    private val tokenParser: TokenParser,
    private val accountService: AccountService,
    private val accountReader: AccountReader,
    private val passwordEncoder: PasswordEncoder,
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
                RequestBodyUsernamePasswordAuthenticationFilter(
                    authenticationManager(),
                    objectMapper,
                    IssueJwtAuthenticationSuccessHandler(tokenService, objectMapper),
                    authenticationFailureHandler(),
                ),
                UsernamePasswordAuthenticationFilter::class.java,
            ).addFilterAfter(
                JwtAuthenticationFilter(
                    BearerTokenResolver(),
                    authenticationManager(),
                    authenticationFailureHandler(),
                ),
                UsernamePasswordAuthenticationFilter::class.java,
            ).addFilterAfter(
                UserDetailsExchangeFilter(objectMapper),
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
    fun authenticationManager(): AuthenticationManager {
        val daoAuthenticationProvider = DaoAuthenticationProvider(passwordEncoder)
        daoAuthenticationProvider.setUserDetailsService(accountService)
        return ProviderManager(JwtAuthenticationProvider(tokenParser, accountReader), daoAuthenticationProvider)
    }

    @Bean
    fun authenticationFailureHandler(): AuthenticationFailureHandler = AuthenticationEntryPointFailureHandler(authenticationEntryPoint())

    @Bean
    fun authenticationEntryPoint(): AuthenticationEntryPoint = ApiAuthenticationEntryPoint(objectMapper)
}
