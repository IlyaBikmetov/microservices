package ru.ibikmetov.microservices.parking.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Table
import org.springframework.stereotype.Component

@Entity
@Table(name = "parking_place")
@Component
data class ParkingPlace (
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,
    @Column(name = "place")
    val place: String?,
    @Column(name = "status_id")
    var statusId: Long?,
) {
    constructor() : this(null,
        null,
        null,
    )
}