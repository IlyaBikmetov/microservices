package ru.ibikmetov.microservices.orders.data

import org.springframework.data.repository.CrudRepository
import ru.ibikmetov.microservices.orders.model.Order
import java.time.LocalDateTime
import java.util.*

interface OrderRepository : CrudRepository<Order, String> {
    fun findByUsernameAndDeleted(username: String?, deleted: LocalDateTime?): List<Order>
    fun findByUsernameAndRequestKey(username: String, requestKey: String): Optional<Order>
    fun findByIdAndDeleted(id: String, deleted: LocalDateTime?): Optional<Order>
    fun findByOrderName(orderName: String?): Optional<Order>
}