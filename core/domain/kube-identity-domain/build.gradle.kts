plugins {
    kotlin("jvm") version "1.9.22"
    `java-library`
}

group = "com.dunkware.kube"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    api("jakarta.persistence:jakarta.persistence-api:3.1.0")
}
