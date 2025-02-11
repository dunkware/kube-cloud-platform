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
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.3")
}
