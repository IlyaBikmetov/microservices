package ru.ibikmetov.microservices.orders.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Table
import org.springframework.stereotype.Component

@Entity
@Table(name = "Ordersversion")
@Component
data class OrdersVersion (
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,
    @Column(name = "username")
    val username: String?,
    var version: Long?,
) {
    constructor() : this(null,
        null,
        null,
    )
}