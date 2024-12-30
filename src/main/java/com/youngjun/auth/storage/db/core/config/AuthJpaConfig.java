package com.youngjun.auth.storage.db.core.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

//TODO
@Configuration
@EnableTransactionManagement
@EntityScan(basePackages = "com.youngjun.auth.storage.db.core")
@EnableJpaRepositories(basePackages = "com.youngjun.auth.storage.db.core")
public class AuthJpaConfig {

}
