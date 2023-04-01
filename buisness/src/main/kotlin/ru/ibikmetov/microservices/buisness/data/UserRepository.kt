package ru.ibikmetov.microservices.buisness.data

import org.springframework.data.repository.CrudRepository
import ru.ibikmetov.microservices.buisness.model.User

interface UserRepository : CrudRepository<User, String> {
    fun findByUsername(username: String?): List<User?>
}