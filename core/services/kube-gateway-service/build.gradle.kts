plugins {
    id("org.springframework.boot") version "2.7.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    java
     id("com.google.cloud.tools.jib") version "3.4.0"

}

group = "com.dunkware.cloud"
version = "0.1.0-SNAPSHOT"
description = "Dunkware Cloud Gateway"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}


repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2021.0.3"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

dependencies {
    // Spring Cloud Gateway
    implementation("org.springframework.cloud:spring-cloud-starter-gateway")
    
    // Eureka Client
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-client")
    
    // Load Balancer
    implementation("org.springframework.cloud:spring-cloud-starter-loadbalancer")
    
    // Spring Security WebFlux
    implementation("org.springframework.boot:spring-boot-starter-security:2.7.1")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    // WebFlux
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    
    // Logging
    implementation("org.fluentd:fluent-logger:0.3.4")
    implementation("com.sndyuk:logback-more-appenders:1.8.3")
    implementation("ch.qos.logback:logback-classic")
    
    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Optional: Configure the Spring Boot plugin
springBoot {
    buildInfo()
}


jib {
    from {
        image = "eclipse-temurin:21-jre-alpine"
        platforms {
            platform {
                architecture = "amd64"
                os = "linux"
            }
        }
    }

    to {
        image = "harbor.dunkware.net/dunkware/cloud-core-gate"
        tags = setOf(project.version.toString(), "latest")

        auth {
            username = "robot\$dunkware+dunkware-gradle-push"
            password = "CHANGE"
        }
    }

    container {
        mainClass = "com.dunkware.cloud.gate.server.GatewayApp"
        jvmFlags = listOf(
            "-XX:+UseContainerSupport",
            "-XX:MaxRAMPercentage=75",
            "-XX:+UseZGC"
        )
        ports = listOf("8080")

        environment = mapOf(
            "SPRING_OUTPUT_ANSI_ENABLED" to "ALWAYS",
            "JAVA_TOOL_OPTIONS" to "-XX:+UseContainerSupport"
        )

        user = "nobody:nobody"
        creationTime = "USE_CURRENT_TIMESTAMP"
    }
}

tasks.register("buildAndPush") {
    group = "docker"
    description = "Builds and pushes Docker image with version tag"
    dependsOn("jib")

    doFirst {
        System.setProperty("jib.allowInsecureRegistries", "true")
    }
}