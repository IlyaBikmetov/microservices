package ru.ibikmetov.microservices.dwh.model

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

data class ParkingDwh(
    @Id
    var id: Long?,
    var username: String?,
    var place: String?,
    var start: LocalDateTime?,
    var stop: LocalDateTime?,
    var money: BigDecimal?,
    var requestKey: String?,
) {
    constructor() : this(null,
        null,
        null,
        null,
        null,
        null,
        null,
    )
}