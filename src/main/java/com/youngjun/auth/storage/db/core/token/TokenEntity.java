package com.youngjun.auth.storage.db.core.token;

import com.youngjun.auth.core.domain.token.Token;
import com.youngjun.auth.storage.db.core.support.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Table(name = "token")
@Entity
public class TokenEntity extends BaseEntity {

    private Long authId;

    private String refreshToken;

    protected TokenEntity() {
    }

    public TokenEntity(Long authId, String refreshToken) {
        this.authId = authId;
        this.refreshToken = refreshToken;
    }

    public Token toToken() {
        return new Token(authId, refreshToken);
    }

    public String getRefreshToken() {
        return refreshToken;
    }
}
