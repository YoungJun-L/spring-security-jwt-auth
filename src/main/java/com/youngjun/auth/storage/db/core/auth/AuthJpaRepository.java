package com.youngjun.auth.storage.db.core.auth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthJpaRepository extends JpaRepository<AuthEntity, Long> {

	Optional<AuthEntity> findByUsername(String username);

	boolean existsByUsername(String username);

}
