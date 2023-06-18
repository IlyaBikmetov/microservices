package ru.ibikmetov.microservices.places.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import ru.ibikmetov.microservices.places.api.v1.*

@RestController
class ControllerV1(private val placeService: PlaceService) {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    @GetMapping("health")
    fun home(): String? {
        return "{\"status\": \"OK\"}"
    }

    @GetMapping("config")
    fun config(): String? {
        return System.getenv().entries.joinToString("\n")
    }

    @RequestMapping("api/v1/read", method = [RequestMethod.POST])
    fun read(@RequestBody parkingRequest: PlaceReadRequest,
             @RequestHeader("x-username") username: String?): PlaceResponse = placeService.read(parkingRequest, username)

    @RequestMapping("api/v1/update", method = [RequestMethod.POST])
    fun update(@RequestBody parkingRequest: PlaceUpdateRequest,
               @RequestHeader("x-username") username: String?,
               @RequestHeader("x-request-key") requestKey: String?): PlaceResponse = placeService.update(parkingRequest, username, requestKey)

    @RequestMapping("api/v1/free", method = [RequestMethod.GET])
    fun free(@RequestHeader("x-username") username: String?): PlacesResponse = placeService.free(username)
}