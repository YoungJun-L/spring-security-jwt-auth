package com.youngjun.auth.storage.db.core.auth;

import com.youngjun.auth.core.domain.auth.Auth;
import com.youngjun.auth.core.domain.auth.AuthStatus;
import com.youngjun.auth.storage.db.core.support.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Table(name = "auth")
@Entity
public class AuthEntity extends BaseEntity {

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private AuthStatus status;

    protected AuthEntity() {
    }

    public AuthEntity(String username, String password, AuthStatus status) {
        this.username = username;
        this.password = password;
        this.status = status;
    }

    public Auth toAuth() {
        return new Auth(getId(), username, password, status);
    }

}
