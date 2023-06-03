package ru.ibikmetov.microservices.dwh.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import ru.ibikmetov.microservices.dwh.api.v1.ParkingDwhRequest
import ru.ibikmetov.microservices.dwh.api.v1.ParkingDwhResponse

@RestController
class ControllerV1(private val parkingDwhService: ParkingDwhService) {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    @GetMapping("health")
    fun home(): String? {
        return "{\"status\": \"OK\"}"
    }

    @GetMapping("config")
    fun config(): String? {
        return System.getenv().entries.joinToString("\n")
    }

    @RequestMapping("api/v1/dwh/put", method = [RequestMethod.POST])
    fun orderCreate(@RequestBody parkingDwhRequest: ParkingDwhRequest,
                    @RequestHeader("x-username") username: String?,
                    @RequestHeader("x-request-key") requestKey: String?): ParkingDwhResponse = parkingDwhService.put(parkingDwhRequest, username, requestKey)
}