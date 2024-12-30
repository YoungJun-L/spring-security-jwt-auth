package com.youngjun.auth.core.api.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youngjun.auth.core.api.security.filter.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Profile("!test")
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    private final AuthenticationSuccessHandler authenticationSuccessHandler;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    private final BearerTokenResolver bearerTokenResolver;

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    private final AuthDetailsExchangeFilter authDetailsExchangeFilter;

    private final ObjectMapper objectMapper;

    public SecurityConfig(AuthenticationSuccessHandler authenticationSuccessHandler, AuthenticationEntryPoint authenticationEntryPoint, JwtAuthenticationProvider jwtAuthenticationProvider, BearerTokenResolver bearerTokenResolver, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, AuthDetailsExchangeFilter authDetailsExchangeFilter, ObjectMapper objectMapper) {
        this.authenticationSuccessHandler = authenticationSuccessHandler;
        this.authenticationEntryPoint = authenticationEntryPoint;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;
        this.bearerTokenResolver = bearerTokenResolver;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.authDetailsExchangeFilter = authDetailsExchangeFilter;
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(authorize -> authorize.requestMatchers(NotFilterRequestMatcher.matchers())
                        .permitAll()
                        .requestMatchers(AntPathRequestMatcher.antMatcher(HttpMethod.GET, "/products/**"))
                        .permitAll()
                        .anyRequest()
                        .authenticated())
                .addFilterAt(requestBodyUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(authDetailsExchangeFilter, AuthorizationFilter.class)
                .exceptionHandling(
                        exceptionHandling -> exceptionHandling.authenticationEntryPoint(authenticationEntryPoint))
                .authenticationManager(authenticationManager())
                .headers(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .requestCache(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(
                        sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        return http.build();
    }

    @Bean
    public RequestBodyUsernamePasswordAuthenticationFilter requestBodyUsernamePasswordAuthenticationFilter() {
        return new RequestBodyUsernamePasswordAuthenticationFilter(authenticationManager(),
                authenticationSuccessHandler, authenticationFailureHandler(), objectMapper);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(bearerTokenResolver, authenticationManager(),
                authenticationFailureHandler());
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider(passwordEncoder);
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return new ProviderManager(jwtAuthenticationProvider, daoAuthenticationProvider);
    }

    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return new AuthenticationEntryPointFailureHandler(authenticationEntryPoint);
    }

}
