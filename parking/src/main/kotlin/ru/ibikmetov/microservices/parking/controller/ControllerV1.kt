package ru.ibikmetov.microservices.parking.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import ru.ibikmetov.microservices.parking.api.v1.*

@RestController
class ControllerV1(private val parkingService: ParkingService) {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    @GetMapping("health")
    fun home(): String? {
        return "{\"status\": \"OK\"}"
    }

    @GetMapping("config")
    fun config(): String? {
        return System.getenv().entries.joinToString("\n")
    }

    @RequestMapping("api/v1/start", method = [RequestMethod.POST])
    fun parkingCreate(@RequestBody parkingRequest: StartRequest,
                      @RequestHeader("x-username") username: String?,
                      @RequestHeader("x-request-key") requestKey: String?): ParkingResponse = parkingService.start(parkingRequest, username, requestKey)
    @RequestMapping("api/v1/read", method = [RequestMethod.GET])
    fun parkingRead(@RequestBody parkingRequest: ParkingId,
                    @RequestHeader("x-username") username: String?): ParkingResponse = parkingService.read(parkingRequest, username)

    @RequestMapping("api/v1/stop", method = [RequestMethod.POST])
    fun parkingStop(@RequestBody parkingRequest: StopRequest,
                    @RequestHeader("x-username") username: String?,
                    @RequestHeader("x-request-key") requestKey: String?): ParkingResponse = parkingService.stop(parkingRequest, username, requestKey)

    @RequestMapping("api/v1/claim", method = [RequestMethod.POST])
    fun parkingClaim(@RequestBody claimRequest: ClaimRequest,
                     @RequestHeader("x-username") username: String?,
                     @RequestHeader("x-request-key") requestKey: String?): Result = parkingService.claim(claimRequest, username, requestKey)
}