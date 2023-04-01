plugins {
    kotlin("jvm")
}

group = "ru.ibikmetov.microservices"
version = "3.0"
java.sourceCompatibility = JavaVersion.VERSION_17

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

subprojects {
    group = rootProject.group
    version = rootProject.version
}