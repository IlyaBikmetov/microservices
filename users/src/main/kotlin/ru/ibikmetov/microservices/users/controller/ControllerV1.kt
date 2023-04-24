package ru.ibikmetov.microservices.users.controller

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import ru.ibikmetov.microservices.users.api.v1.UserCreateUpdateRequest
import ru.ibikmetov.microservices.users.api.v1.UserReadDeleteRequest
import ru.ibikmetov.microservices.users.api.v1.UserResponse

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

    @RequestMapping("api/v1/user/create", method = [ RequestMethod.GET, RequestMethod.POST ])
    fun userCreate(@RequestBody userRequest: UserCreateUpdateRequest): UserResponse = userService.create(userRequest)

    @RequestMapping("api/v1/user/update", method = [ RequestMethod.GET, RequestMethod.POST ])
    fun userUpdate(@RequestBody userRequest: UserCreateUpdateRequest,
                   @RequestHeader("x-username") username: String?): UserResponse = userService.update(userRequest, username)

    @RequestMapping("api/v1/user/read", method = [ RequestMethod.GET, RequestMethod.POST ])
    fun userRead(@RequestBody userRequest: UserReadDeleteRequest,
                 @RequestHeader("x-username") username: String?): UserResponse = userService.read(userRequest, username)

    @RequestMapping("api/v1/user/delete", method = [ RequestMethod.GET, RequestMethod.POST ])
    fun userDelete(@RequestBody userRequest: UserReadDeleteRequest,
                   @RequestHeader("x-username") username: String?): UserResponse = userService.delete(userRequest, username)
}