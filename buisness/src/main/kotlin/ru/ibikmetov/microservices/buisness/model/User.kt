package ru.ibikmetov.microservices.buisness.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Table
import org.springframework.stereotype.Component

@Entity
@Table(name = "Users")
@Component
data class User (
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,
    val username: String?,
    @Column(name = "firstname")
    val firstName: String?,
    @Column(name = "lastname")
    val lastName: String?,
    val email: String?,
    val phone: String?,
) {
    constructor() : this(null,
        null,
        null,
        null,
        null,
        null
    )
}