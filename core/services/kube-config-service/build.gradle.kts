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
    implementation("org.springframework.cloud:spring-cloud-config-server")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

extra["springCloudVersion"] = "2023.0.0"

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
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
        image = "harbor.dunkware.net/dunkware/cloud-infra-config"
        tags = setOf(project.version.toString(), "latest")

        auth {
            username = "robot\$dunkware+dunkware-gradle-push"
            password = "CHANGE"
        }
    }

    container {
        mainClass = "com.dunkware.cloud.config.ConfigServiceApplicationKt"
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