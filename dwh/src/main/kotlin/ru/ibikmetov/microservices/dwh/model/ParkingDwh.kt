package ru.ibikmetov.microservices.dwh.model

import jakarta.persistence.*
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "dwh")
@Component
data class ParkingDwh(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long?,
    @Column(name = "username")
    var username: String?,
    @Column(name = "numbervehicle")
    var numberVehicle: String?,
    @Column(name = "place")
    var place: String?,
    @Column(name = "start")
    var start: LocalDateTime?,
    @Column(name = "stop")
    var stop: LocalDateTime?,
    @Column(name = "money")
    var money: BigDecimal?,
    @Column(name = "requestkey")
    var requestKey: String?,
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