package ru.ibikmetov.microservices.users.data

import org.springframework.data.repository.CrudRepository
import ru.ibikmetov.microservices.users.model.User

interface UserRepository : CrudRepository<User, String> {
    fun findByUsername(username: String?): List<User?>
}