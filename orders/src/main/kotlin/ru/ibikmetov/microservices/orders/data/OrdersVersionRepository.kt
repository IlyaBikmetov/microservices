package ru.ibikmetov.microservices.orders.data

import org.springframework.data.repository.CrudRepository
import ru.ibikmetov.microservices.orders.model.OrdersVersion

interface OrdersVersionRepository : CrudRepository<OrdersVersion, String> {
    fun findByUsername(userName: String?): List<OrdersVersion>
}