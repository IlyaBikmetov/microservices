package ru.ibikmetov.microservices.inspector.data

import org.springframework.data.repository.CrudRepository
import ru.ibikmetov.microservices.inspector.model.ClaimDB
import java.util.*

interface ClaimRepository : CrudRepository<ClaimDB, String> {
    fun findByRequestKey(requestKey: String): Optional<ClaimDB>
}