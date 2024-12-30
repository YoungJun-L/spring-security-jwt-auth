package com.youngjun.auth.core.api.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.youngjun.auth.core.api.security.response.LoginResponseDto;
import com.youngjun.auth.core.api.support.response.AuthResponse;
import com.youngjun.auth.core.domain.auth.Auth;
import com.youngjun.auth.core.domain.token.TokenPair;
import com.youngjun.auth.core.domain.token.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class IssueJwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenService tokenService;

    private final ObjectMapper objectMapper;

    public IssueJwtAuthenticationSuccessHandler(TokenService tokenService, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        Auth auth = (Auth) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        TokenPair tokenPair = tokenService.issue(auth);
        AuthResponse<?> authResponse = AuthResponse.success(LoginResponseDto.from(tokenPair));
        write(response, authResponse);
    }

    private void write(HttpServletResponse response, AuthResponse<?> authResponse) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(authResponse));
    }

}
