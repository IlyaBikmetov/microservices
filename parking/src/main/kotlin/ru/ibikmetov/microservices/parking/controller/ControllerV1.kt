package ru.ibikmetov.microservices.parking.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import ru.ibikmetov.microservices.parking.api.v1.*
import java.net.HttpURLConnection
import java.net.URL

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
    fun orderCreate(@RequestBody parkingRequest: StartRequest,
                   @RequestHeader("x-username") username: String?,
                   @RequestHeader("x-request-key") requestKey: String?): ParkingResponse = parkingService.start(parkingRequest, username, requestKey)
    @RequestMapping("api/v1/read", method = [RequestMethod.GET])
    fun userRead(@RequestBody parkingRequest: ParkingId,
                 @RequestHeader("x-username") username: String?): ParkingResponse = parkingService.read(parkingRequest, username)

    @RequestMapping("api/v1/stop", method = [RequestMethod.POST])
    fun userRead(@RequestBody parkingRequest: StopRequest,
                 @RequestHeader("x-username") username: String?,
                 @RequestHeader("x-request-key") requestKey: String?): ParkingResponse = parkingService.stop(parkingRequest, username, requestKey)
}