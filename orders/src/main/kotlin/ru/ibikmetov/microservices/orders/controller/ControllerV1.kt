package ru.ibikmetov.microservices.orders.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import ru.ibikmetov.microservices.orders.api.v1.*

@RestController
class ControllerV1(private val orderService: OrderService) {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    @GetMapping("health")
    fun home(): String? {
        return "{\"status\": \"OK\"}"
    }

    @GetMapping("config")
    fun config(): String? {
        return System.getenv().entries.joinToString("\n")
    }

    @RequestMapping("api/v1/order/create", method = [RequestMethod.POST])
    fun orderCreate(@RequestBody orderRequest: OrderCreateUpdateRequest,
                   @RequestHeader("x-username") username: String?,
                   @RequestHeader("x-request-key") requestKey: String?,): OrderResponse = orderService.create(orderRequest, username, requestKey)

    @RequestMapping("api/v1/order/update", method = [RequestMethod.POST])
    fun orderUpdate(@RequestBody orderRequest: OrderCreateUpdateRequest,
                   @RequestHeader("x-username") username: String?): OrderResponse = orderService.update(orderRequest, username)

    @RequestMapping("api/v1/order/read", method = [RequestMethod.POST])
    fun userRead(@RequestBody orderRequest: OrderReadDeleteRequest,
                 @RequestHeader("x-username") username: String?): OrderResponse = orderService.read(orderRequest, username)

    @RequestMapping("api/v1/order/delete", method = [RequestMethod.POST])
    fun orderDelete(@RequestBody orderRequest: OrderReadDeleteRequest,
                    @RequestHeader("x-username") username: String?): OrderResponse = orderService.delete(orderRequest, username)

    @RequestMapping("api/v1/order/orders", method = [RequestMethod.POST])
    fun ordersList(@RequestBody orderRequest: OrdersRequest,
                   @RequestHeader("x-username") username: String?): OrdersResponse = orderService.orders(orderRequest, username)
}