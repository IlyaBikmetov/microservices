package ru.ibikmetov.microservices.money.model

import jakarta.persistence.*
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "money")
@Component
data class Money(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long?,
    @Column(name = "username")
    var username: String?,
    @Column(name = "money")
    var money: BigDecimal?,
    @Column(name = "hold_money")
    var holdMoney: BigDecimal?,
    @Column(name = "last_operation")
    var lastOperation: String?,
    @Column(name = "last_value")
    var lastValue: BigDecimal?,
    @Column(name = "requestkey")
    var requestKey: String?,
    @Column(name = "updated")
    var updated: LocalDateTime?,
) {
    constructor() : this(null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    )
}