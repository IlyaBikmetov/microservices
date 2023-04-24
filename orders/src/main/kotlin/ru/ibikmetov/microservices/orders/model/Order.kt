package ru.ibikmetov.microservices.orders.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Table
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Entity
@Table(name = "Orders")
@Component
data class Order (
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,
    @Column(name = "username")
    val username: String?,
    @Column(name = "ordername")
    val orderName: String?,
    val quantity: Long?,
    @Column(name = "requestkey")
    val requestKey: String?,
    var deleted: LocalDateTime?,
) {
    constructor() : this(null,
        null,
        null,
        null,
        null,
        null,
    )
}