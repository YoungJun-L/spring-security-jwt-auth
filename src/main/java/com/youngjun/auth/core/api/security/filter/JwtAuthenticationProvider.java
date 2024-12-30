package com.youngjun.auth.core.api.security.filter;

import com.youngjun.auth.core.domain.auth.Auth;
import com.youngjun.auth.core.domain.auth.AuthService;
import com.youngjun.auth.core.domain.token.TokenParser;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

    private final TokenParser tokenParser;

    private final AuthService authService;

    public JwtAuthenticationProvider(TokenParser tokenParser, AuthService authService) {
        this.tokenParser = tokenParser;
        this.authService = authService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) {
        String token = (String) authentication.getPrincipal();
        try {
            String username = tokenParser.parseSubject(token);
            return createSuccessAuthentication(username);
        } catch (Exception ex) {
            throw new BadTokenException(ex.getMessage(), ex.getCause());
        }
    }

    private Authentication createSuccessAuthentication(String username) {
        Auth auth = authService.loadUserByUsername(username);
        return JwtAuthenticationToken.authenticated(auth);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return BearerTokenAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
