package ru.ibikmetov.microservices.parking.data

import org.springframework.data.repository.CrudRepository
import ru.ibikmetov.microservices.parking.api.v1.ParkingStatus
import ru.ibikmetov.microservices.parking.model.Parking
import ru.ibikmetov.microservices.parking.model.ParkingPlace
import java.util.*

interface ParkingPlaceRepository : CrudRepository<ParkingPlace, String> {
    fun findByStatusId(statusId: Int): List<ParkingPlace>
}