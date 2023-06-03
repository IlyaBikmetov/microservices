package ru.ibikmetov.microservices.money.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import ru.ibikmetov.microservices.money.api.v1.*

@RestController
class ControllerV1(private val moneyService: MoneyService) {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    @GetMapping("health")
    fun home(): String? {
        return "{\"status\": \"OK\"}"
    }

    @GetMapping("config")
    fun config(): String? {
        return System.getenv().entries.joinToString("\n")
    }

    @RequestMapping("api/v1/money/check", method = [RequestMethod.POST])
    fun check(@RequestBody checkRequest: CheckRequest,
              @RequestHeader("x-username") username: String?,
              @RequestHeader("x-request-key") requestKey: String?): MoneyResponse = moneyService.check(checkRequest, username)

    @RequestMapping("api/v1/money/operation", method = [RequestMethod.POST])
    fun operation(@RequestBody operationRequest: OperationRequest,
                  @RequestHeader("x-username") username: String?,
                  @RequestHeader("x-request-key") requestKey: String?): MoneyResponse = moneyService.operation(operationRequest, username, requestKey)
}