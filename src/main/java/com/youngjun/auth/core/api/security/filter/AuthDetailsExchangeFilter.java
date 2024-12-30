package com.youngjun.auth.core.api.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

@Component
public class AuthDetailsExchangeFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public AuthDetailsExchangeFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (Objects.isNull(authentication)) {
            throw new AuthenticationServiceException("Authentication object should not be null.");
        }
        AuthDetails authDetails = AuthDetails.from(authentication);
        String token = objectMapper.writeValueAsString(authDetails);
        AuthRequest authRequest = new AuthRequest(request, token);
        SecurityContextHolder.clearContext();
        filterChain.doFilter(authRequest, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return Arrays.stream(NotFilterRequestMatcher.matchers()).anyMatch(m -> m.matches(request));
    }

    private static class AuthRequest extends HttpServletRequestWrapper {

        private static final String AUTH_COOKIE_NAME = "user";

        private final Cookie[] cookies;

        private AuthRequest(HttpServletRequest request, String token) {
            super(request);
            Cookie[] original = Optional.ofNullable(request.getCookies()).orElse(new Cookie[]{});
            cookies = Arrays.copyOf(original, original.length + 1);
            cookies[original.length] = new Cookie(AUTH_COOKIE_NAME, token);
        }

        @Override
        public Cookie[] getCookies() {
            return cookies;
        }

    }

    private record AuthDetails(Object id, Object details) {
        private static AuthDetails from(Authentication authentication) {
            if (authentication instanceof AnonymousAuthenticationToken) {
                return new AuthDetails(-1L, null);
            }
            return new AuthDetails(authentication.getPrincipal(), authentication.getDetails());
        }
    }

}
