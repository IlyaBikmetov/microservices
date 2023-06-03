package ru.ibikmetov.microservices.parking.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class MicroservicesConfig {
    @Value("\${microservices.money.url}")
    val moneyURL: String? = null

    @Value("\${microservices.dwh.url}")
    val dwhURL: String? = null
}