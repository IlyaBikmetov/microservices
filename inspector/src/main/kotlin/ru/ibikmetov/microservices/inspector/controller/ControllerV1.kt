package ru.ibikmetov.microservices.inspector.controller

import org.slf4j.LoggerFactory
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.web.bind.annotation.*
import ru.ibikmetov.microservices.inspector.api.v1.*
import ru.ibikmetov.microservices.parking.model.Claim

@RestController
class ControllerV1(private val inspectorService: InspectorService) {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    @GetMapping("health")
    fun home(): String? {
        return "{\"status\": \"OK\"}"
    }

    @KafkaListener(id = "claim", topics = ["parking_claims"], containerFactory = "kafkaListenerContainerFactory")
    fun claimingListener(claim: Claim?): Result = inspectorService.claim(claim)

    @RequestMapping("api/v1/add", method = [RequestMethod.POST])
    fun claim(@RequestBody parkingRequest: ClaimRequest,
              @RequestHeader("x-username") username: String?,
              @RequestHeader("x-request-key") requestKey: String?): Result = inspectorService.claim(parkingRequest, username, requestKey)
}