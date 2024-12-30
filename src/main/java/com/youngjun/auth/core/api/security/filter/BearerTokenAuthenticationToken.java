package com.youngjun.auth.core.api.security.filter;

import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.util.Collections;
import java.util.Objects;

public class BearerTokenAuthenticationToken extends AbstractAuthenticationToken {

    private final String token;

    public BearerTokenAuthenticationToken(String token) {
        super(Collections.emptyList());
        this.token = token;
    }

    @Override
    public Object getCredentials() {
        return this.token;
    }

    @Override
    public Object getPrincipal() {
        return this.token;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        BearerTokenAuthenticationToken that = (BearerTokenAuthenticationToken) o;
        return Objects.equals(token, that.token);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Objects.hashCode(token);
        return result;
    }
}
