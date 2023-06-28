package ru.ibikmetov.microservices.parking.model

data class Claim (
    var place: String?,
    var username: String?,
    var requestKey: String?,
)