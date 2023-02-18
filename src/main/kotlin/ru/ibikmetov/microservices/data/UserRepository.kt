package ru.ibikmetov.microservices.data

import org.springframework.data.repository.CrudRepository
import ru.ibikmetov.microservices.model.User

interface UserRepository : CrudRepository<User, String>