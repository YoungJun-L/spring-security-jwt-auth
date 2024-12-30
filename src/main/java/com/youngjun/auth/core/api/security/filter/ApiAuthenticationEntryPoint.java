package com.youngjun.auth.core.api.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youngjun.auth.core.api.support.error.AuthErrorType;
import com.youngjun.auth.core.api.support.response.AuthResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class ApiAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(ApiAuthenticationEntryPoint.class);

    private final ObjectMapper objectMapper;

    public ApiAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ex)
            throws IOException {
        AuthErrorType authErrorType = resolve(ex);
        log(ex, authErrorType);
        write(response, authErrorType);
    }

    private AuthErrorType resolve(AuthenticationException ex) {
        AuthErrorType authErrorType;
        if (ex instanceof BadTokenException) {
            authErrorType = AuthErrorType.TOKEN_INVALID_ERROR;
        } else if (ex instanceof BadCredentialsException) {
            authErrorType = AuthErrorType.AUTH_BAD_CREDENTIALS_ERROR;
        } else {
            authErrorType = AuthErrorType.UNAUTHORIZED_ERROR;
        }
        return authErrorType;
    }

    private void log(AuthenticationException ex, AuthErrorType authErrorType) {
        switch (authErrorType.getLogLevel()) {
            case ERROR -> log.error("AuthException : {}", ex.getMessage(), ex);
            case WARN -> log.warn("AuthException : {}", ex.getMessage(), ex);
            default -> log.info("AuthException : {}", ex.getMessage(), ex);
        }
    }

    private void write(HttpServletResponse response, AuthErrorType authErrorType) throws IOException {
        response.setStatus(authErrorType.getStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(AuthResponse.error(authErrorType)));
    }

}
