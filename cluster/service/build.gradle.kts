plugins {
    id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
    id("java")
}

group = "org.dunkware.cloud"
version = "0.0.1-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {

    // Internal dependencies
    implementation("org.dunkware.cloud:kube-cluster-domain:0.0.1-SNAPSHOT")
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("io.kubernetes:client-java:18.0.0")
    implementation("io.kubernetes:client-java-api:18.0.0")
    implementation("io.kubernetes:client-java-extended:18.0.0")


    // Metrics and Monitoring
    implementation("io.micrometer:micrometer-registry-prometheus")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}