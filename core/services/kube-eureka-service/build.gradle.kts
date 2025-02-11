import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.google.cloud.tools.jib.gradle.JibTask

plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    id("com.google.cloud.tools.jib") version "3.4.0"
}

group = "com.dunkware.cloud"
version = "0.1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.cloud:spring-cloud-starter-netflix-eureka-server")
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.cloud:spring-cloud-starter-config")
    implementation("org.springframework.cloud:spring-cloud-starter-bootstrap")

}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:2023.0.0")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
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
        image = "harbor.dunkware.net/dunkware/cloud-infra-eureka"
        tags = setOf(project.version.toString(), "latest")
        auth {
            username = "robot\$dunkware+dunkware-gradle-push"
            password = "CHANGFE"
        }
    }
    container {
        mainClass = "com.dunkware.cloud.eureka.EurekaServiceApplicationKt"
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