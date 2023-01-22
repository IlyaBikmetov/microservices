import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.2"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.bmuschko.docker-spring-boot-application") version "7.4.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
}

group = "ru.ibikmetov"
version = "1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

docker {
    springBootApplication {
        baseImage.set("openjdk:17-alpine")
        maintainer.set("(c) Bikmetov")
        ports.set(listOf(8000))
        jvmArgs.set(listOf("-Dspring.profiles.active=production", "-Xmx2048m"))
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "ru.ibikmetov.microservices.MicroservicesApplication"
    }
}