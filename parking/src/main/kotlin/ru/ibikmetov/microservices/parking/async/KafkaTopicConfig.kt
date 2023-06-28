package ru.ibikmetov.microservices.parking.async

import org.apache.kafka.clients.admin.AdminClientConfig
import org.apache.kafka.clients.admin.NewTopic
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaAdmin

@Configuration
class KafkaTopicConfig {
    @Value(value = "\${spring.kafka.bootstrap-servers}")
    private val bootstrapAddress: String? = null

    @Bean
    fun kafkaAdmin(): KafkaAdmin {
        val configs: MutableMap<String, Any> = HashMap()
        configs[AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress as Any
        return KafkaAdmin(configs)
    }

    @Bean
    fun topicParkingClaims(): NewTopic {
        return NewTopic("parking_claims", 1, 1.toShort())
    }
}