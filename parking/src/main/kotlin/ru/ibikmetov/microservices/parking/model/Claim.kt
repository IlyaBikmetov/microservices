package ru.ibikmetov.microservices.parking.model

data class Claim (
    val place: String?,
    var username: String?,
    var requestKey: String?,
    )