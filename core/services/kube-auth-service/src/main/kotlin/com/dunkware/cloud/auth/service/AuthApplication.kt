package com.dunkware.cloud.auth.service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication(
    scanBasePackages = ["com.dunkware.cloud.auth.service","com.dunkware.cloud.auth.starter"]
)
@EnableFeignClients
@EnableDiscoveryClient  // Enables service discovery
class AuthApplication


fun main(args: Array<String>) {
    runApplication<AuthApplication>(*args)
}