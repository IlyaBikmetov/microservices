package ru.ibikmetov.microservices.places.data

import org.springframework.data.repository.CrudRepository
import ru.ibikmetov.microservices.places.model.Place

interface PlaceRepository : CrudRepository<Place, String> {
    fun findByStatusId(statusId: Int): List<Place>
}