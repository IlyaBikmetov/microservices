package ru.ibikmetov.microservices.dwh.data

import org.springframework.data.repository.CrudRepository
import ru.ibikmetov.microservices.dwh.model.ParkingDwh
import java.util.Optional

interface ParkingDwhRepository : CrudRepository<ParkingDwh, String> {
    fun findByUsernameAndRequestKey(username: String, requestKey: String): Optional<ParkingDwh>
}