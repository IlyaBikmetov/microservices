package ru.ibikmetov.microservices.dwh.data

import org.springframework.data.mongodb.repository.MongoRepository
import ru.ibikmetov.microservices.dwh.model.ParkingDwh
import java.util.*


interface ParkingDwhRepository : MongoRepository<ParkingDwh?, String?>{
    fun findByUsernameAndRequestKey(username: String, requestKey: String): Optional<ParkingDwh>
}