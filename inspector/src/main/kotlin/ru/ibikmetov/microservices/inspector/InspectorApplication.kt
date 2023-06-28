package ru.ibikmetov.microservices.inspector

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class InspectorApplication

fun main(args: Array<String>) {
    runApplication<InspectorApplication>(*args)
}
