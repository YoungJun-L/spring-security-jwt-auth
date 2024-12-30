package com.youngjun.auth.core.api.security.filter;

import com.youngjun.auth.core.domain.auth.Auth;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final Long userId;

    private JwtAuthenticationToken(Long userId, Map<String, String> details,
                                   Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = userId;
        setDetails(details);
        super.setAuthenticated(true);
    }

    public static JwtAuthenticationToken authenticated(Auth auth) {
        return new JwtAuthenticationToken(auth.id(), auth.details(), auth.getAuthorities());
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        JwtAuthenticationToken that = (JwtAuthenticationToken) o;
        return Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(userId);
        return result;
    }
}
