package com.dunkware.cloud.user.service.config

import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableJpaAuditing
@EnableTransactionManagement
@EntityScan(basePackages = ["com.dunkware.cloud.user.service.entity"])
@EnableJpaRepositories(basePackages = ["com.dunkware.cloud.user.service.repository"])
class JpaConfig