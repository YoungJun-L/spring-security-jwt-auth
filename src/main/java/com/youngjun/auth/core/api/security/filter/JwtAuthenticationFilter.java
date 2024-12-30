package com.youngjun.auth.core.api.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final BearerTokenResolver bearerTokenResolver;

    private final AuthenticationManager authenticationManager;

    private final AuthenticationFailureHandler authenticationFailureHandler;

    public JwtAuthenticationFilter(BearerTokenResolver bearerTokenResolver, AuthenticationManager authenticationManager, AuthenticationFailureHandler authenticationFailureHandler) {
        this.bearerTokenResolver = bearerTokenResolver;
        this.authenticationManager = authenticationManager;
        this.authenticationFailureHandler = authenticationFailureHandler;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = bearerTokenResolver.resolve(request);
            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }
            Authentication authRequest = new BearerTokenAuthenticationToken(token);
            Authentication authResult = authenticationManager.authenticate(authRequest);
            successfulAuthentication(authResult);
        } catch (AuthenticationException ex) {
            unsuccessfulAuthentication(request, response, ex);
            return;
        }
        filterChain.doFilter(request, response);
    }

    private void successfulAuthentication(Authentication authResult) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authResult);
        SecurityContextHolder.setContext(context);
    }

    private void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            AuthenticationException failed) throws ServletException, IOException {
        SecurityContextHolder.clearContext();
        authenticationFailureHandler.onAuthenticationFailure(request, response, failed);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return Arrays.stream(NotFilterRequestMatcher.matchers()).anyMatch(m -> m.matches(request));
    }

}
