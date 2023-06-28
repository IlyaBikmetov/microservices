package ru.ibikmetov.microservices.money.data

import org.springframework.data.repository.CrudRepository
import ru.ibikmetov.microservices.money.model.Money
import java.util.*

interface MoneyRepository : CrudRepository<Money, String> {
    fun findByUsername(username: String): Optional<Money>
    fun findByUsernameAndLastOperationAndRequestKey(username: String, lastOperation: String, requestKey: String): Optional<Money>
    fun findByUsernameAndLastOperationAndLastValueAndRequestKey(username: String, lastOperation: String, lastValue: String, requestKey: String): Optional<Money>
}