package com.dunkware.cloud.user

import jakarta.persistence.EntityManagerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.Primary
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import javax.sql.DataSource

@SpringBootApplication(
    scanBasePackages = ["com.dunkware.cloud.user.service", "com.dunkware.cloud.auth.starter"]
)
@EnableDiscoveryClient
class UserServiceApplication

fun main(args: Array<String>) {
    runApplication<UserServiceApplication>(*args)
}

@Configuration
class DatabaseConfiguration {

    @Bean
    fun flyway(): org.flywaydb.core.Flyway {
        return org.flywaydb.core.Flyway.configure()
            .dataSource(dataSource())
            .load()
    }

    @Bean
    @Primary
    @DependsOn("flyway")
    fun entityManagerFactory(dataSource: DataSource): LocalContainerEntityManagerFactoryBean {
        val factory = LocalContainerEntityManagerFactoryBean()
        factory.dataSource = dataSource
        factory.jpaVendorAdapter = HibernateJpaVendorAdapter()
        factory.setPackagesToScan("com.dunkware.cloud.user")
        return factory
    }

    @Bean
    fun dataSource(): DataSource {
        return org.springframework.boot.jdbc.DataSourceBuilder.create().build()
    }
}