package ru.ibikmetov.microservices.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.ibikmetov.api.v1.UserCreateUpdateRequest
import ru.ibikmetov.api.v1.UserReadDeleteRequest
import ru.ibikmetov.api.v1.UserResponse

@RestController
class ControllerV1(private val userService: UserService) {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    @GetMapping("health")
    fun home(): String? {
        return "{\"status\": \"OK\"}"
    }

    @GetMapping("config")
    fun config(): String? {
        return System.getenv().entries.joinToString("\n")
    }

    @PostMapping("api/v1/user/create")
    fun userCreate(@RequestBody userRequest: UserCreateUpdateRequest): UserResponse = userService.create(userRequest)

    @PostMapping("api/v1/user/update")
    fun userUpdate(@RequestBody userRequest: UserCreateUpdateRequest): UserResponse = userService.update(userRequest)

    @PostMapping("api/v1/user/read")
    fun userRead(@RequestBody userRequest: UserReadDeleteRequest): UserResponse = userService.read(userRequest)

    @PostMapping("api/v1/user/delete")
    fun userDelete(@RequestBody userRequest: UserReadDeleteRequest): UserResponse = userService.delete(userRequest)
}