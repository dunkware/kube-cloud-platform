import org.jetbrains.kotlin.ir.backend.js.lower.collectNativeImplementations

plugins {
    kotlin("jvm") version "1.9.22"  // or whatever version you're using
    // If you need Spring:
   // id("org.springframework.boot") version "3.2.2"
    id("io.spring.dependency-management") version "1.1.4"
}
ext["spring-boot.version"] = "3.2.2"

repositories {
    mavenCentral()
}


dependencyManagement {
    imports {
        mavenBom("org.springframework.boot:spring-boot-dependencies:${ext["spring-boot.version"]}")
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")
    
    repositories {
        mavenCentral()
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

dependencies {
    // Dependencies on included builds
    // The path should match the root project name in each included build's settings.gradle.kts


   // implementation("com.dunkware.auth:dp-auth-model:0.1.0-SNAPSHOT")
   //  implementation("com.dunkware.auth:dp-auth-domain:0.1.0-SNAPSHOT")
   // implementation("com.dunkware.auth:dp-auth-service:0.1.0-SNAPSHOT")
  //  implementation("com.dunkware.auth:dp-auth-starter:0.1.0-SNAPSHOT")


}

