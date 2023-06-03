package ru.ibikmetov.microservices.parking.data

import org.springframework.data.repository.CrudRepository
import ru.ibikmetov.microservices.parking.model.Parking
import java.util.*

interface ParkingRepository : CrudRepository<Parking, String> {
    fun findByUsernameAndRequestKey(username: String, requestKey: String): Optional<Parking>
}