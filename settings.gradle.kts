rootProject.name = "microservices"

pluginManagement {
    plugins {
        val kotlinVersion: String by settings
        val openapiVersion: String by settings
        val bmuschkoVersion: String by settings

        kotlin("jvm") version kotlinVersion apply false
        id("org.openapi.generator") version openapiVersion apply false

        val springBootVersion: String by settings
        val springDependencyVersion: String by settings
        val springPluginVersion: String by settings

        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyVersion
        id("com.bmuschko.docker-java-application") version bmuschkoVersion
        id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion
        id("org.jetbrains.kotlin.plugin.allopen") version kotlinVersion
        kotlin("plugin.spring") version springPluginVersion
    }
}

include("auth")
include("buisness")