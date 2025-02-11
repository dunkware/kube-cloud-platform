plugins {
    id("org.springframework.boot") version "3.2.2" apply false
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    `java-library`
}

group = "com.dunkware.cloud"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

// This is crucial - it applies Spring's dependency management without the full boot plugin
dependencyManagement {
    imports {
        mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
    }
}

dependencies {
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-web")  // Added for HttpServletRequest/Response
    implementation("org.springframework.boot:spring-boot-starter-aop")  // Add this for aspect support

    api("io.jsonwebtoken:jjwt-api:0.12.3")
    implementation("io.jsonwebtoken:jjwt-impl:0.12.3")

    api("io.jsonwebtoken:jjwt-api:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.3")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.3")

    // Optional: Add if you want to make it a proper starter with auto-configuration
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    api("org.springframework.boot:spring-boot-autoconfigure")
}

// This ensures our library can be used as a Spring Boot starter
tasks.jar {
    manifest {
        attributes(
            "Implementation-Title" to project.name,
            "Implementation-Version" to project.version
        )
    }
}

tasks.jar {
    enabled = true
}

java {
    withSourcesJar()
    withJavadocJar()
}