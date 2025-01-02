package com.youngjun.auth.storage.db.core.token;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TokenJpaRepository extends JpaRepository<TokenEntity, Long> {

    List<TokenEntity> findByAuthId(Long authId);

    List<TokenEntity> findByRefreshToken(String refreshToken);

    void deleteByAuthId(Long authId);
}
