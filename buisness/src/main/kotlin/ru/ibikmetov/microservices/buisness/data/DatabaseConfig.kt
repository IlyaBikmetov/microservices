package ru.ibikmetov.microservices.buisness.data

import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.datasource.DriverManagerDataSource
import ru.ibikmetov.microservices.buisness.controller.ControllerV1
import javax.sql.DataSource

@Configuration
class DatabaseConfig {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    @Value("\${spring.datasource.driver-class-name}")
    private val driverClassName: String? = null

    @Value("\${spring.datasource.url}")
    private val url: String? = null

    @Value("\${spring.datasource.username}")
    private val username: String? = null

    @Value("\${spring.datasource.password}")
    private val password: String? = null

    @Bean
    fun dataSource(): DataSource {
        val dataSource = DriverManagerDataSource()
        dataSource.setDriverClassName(driverClassName ?: "org.postgresql.Driver")
        dataSource.url = url
        dataSource.username = username
        dataSource.password = password
        return dataSource
    }
}