package ru.ibikmetov.microservices.inspector.async

import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.annotation.EnableKafka
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.support.serializer.JsonDeserializer
import ru.ibikmetov.microservices.parking.model.Claim

@EnableKafka
@Configuration
class KafkaConsumerConfig {
    @Value(value = "\${spring.kafka.bootstrap-servers}")
    private val bootstrapAddress: String? = null

    @Bean
    fun claimConsumerFactory(): ConsumerFactory<String, Claim> {
        val props: MutableMap<String, Any> = HashMap()
        props[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapAddress as Any
        props[ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG] = StringDeserializer::class.java
        props[ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG] = JsonDeserializer::class.java
        props[JsonDeserializer.TRUSTED_PACKAGES] = "*"
        return DefaultKafkaConsumerFactory(props)
    }

    @Bean
    fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<String, Claim> {
        val factory: ConcurrentKafkaListenerContainerFactory<String, Claim> = ConcurrentKafkaListenerContainerFactory()
        factory.consumerFactory = claimConsumerFactory()
        factory.setConcurrency(1)
        return factory
    }

}