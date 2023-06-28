package ru.ibikmetov.microservices.inspector.model

import jakarta.persistence.*
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Entity
@Table(name = "claims")
@Component
data class ClaimDB (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    var id: Long?,
    @Column(name = "place")
    var place: String?,
    @Column(name = "requestkey")
    var requestKey: String?,
    @Column(name = "updated")
    var updated: LocalDateTime?,
) {
    constructor() : this(null,
        null,
        null,
        null,
    )
}