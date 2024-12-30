package com.youngjun.auth.storage.db.core.auth;

import com.youngjun.auth.core.domain.auth.Auth;
import com.youngjun.auth.core.domain.auth.AuthRepository;
import com.youngjun.auth.core.domain.auth.NewAuth;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class AuthCoreRepository implements AuthRepository {

    private final AuthJpaRepository authJpaRepository;

    public AuthCoreRepository(AuthJpaRepository authJpaRepository) {
        this.authJpaRepository = authJpaRepository;
    }

    public Auth write(NewAuth newAuth) {
        AuthEntity savedAuth = authJpaRepository
                .save(new AuthEntity(newAuth.username(), newAuth.password(), newAuth.status()));
        return savedAuth.toAuth();
    }

    public Optional<Auth> read(String username) {
        return authJpaRepository.findByUsername(username).map(AuthEntity::toAuth);
    }

    public boolean existsByUsername(String username) {
        return authJpaRepository.existsByUsername(username);
    }

    public Optional<Auth> read(Long id) {
        return authJpaRepository.findById(id).map(AuthEntity::toAuth);
    }

}
