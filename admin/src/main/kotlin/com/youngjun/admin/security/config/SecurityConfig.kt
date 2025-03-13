package com.youngjun.admin.security.config

import com.youngjun.admin.security.handler.AdminAuthenticationFailureHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain

@EnableWebSecurity
@Configuration
private class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain =
        http
            .authorizeHttpRequests {
                it
                    .requestMatchers("/signup", "/login", "/css/**", "/js/**", "/img/**")
                    .permitAll()
                    .anyRequest()
                    .hasRole("ADMIN")
            }.formLogin {
                it
                    .loginPage("/login")
                    .failureHandler(AdminAuthenticationFailureHandler())
            }.logout(withDefaults())
            .cors(withDefaults())
            .build()

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()
}
