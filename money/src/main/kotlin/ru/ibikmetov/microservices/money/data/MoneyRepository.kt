package ru.ibikmetov.microservices.money.data

import org.springframework.data.repository.CrudRepository
import ru.ibikmetov.microservices.money.model.Money
import java.util.*

interface MoneyRepository : CrudRepository<Money, String> {
    fun findByUsername(username: String): Optional<Money>
    fun findByUsernameAndRequestKey(username: String, requestKey: String): Optional<Money>
}