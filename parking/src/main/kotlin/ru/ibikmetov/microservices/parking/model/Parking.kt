package ru.ibikmetov.microservices.parking.model

import jakarta.persistence.*
import org.springframework.stereotype.Component
import ru.ibikmetov.microservices.parking.api.v1.ParkingStatus
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "parking")
@Component
data class Parking(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long?,
    @Column(name = "username")
    var username: String?,
    @Column(name = "numbervehicle")
    var numberVehicle: String?,
    @Column(name = "place_id")
    var placeId: Long?,
    @Column(name = "start")
    var start: LocalDateTime?,
    @Column(name = "stop")
    var stop: LocalDateTime?,
    @Column(name = "money")
    var money: BigDecimal?,
    @Column(name = "requestkey")
    var requestKey: String?,
    @Column(name = "updated")
    var updated: LocalDateTime?
) {
    constructor() : this(null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
        null,
    )
}