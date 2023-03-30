package ru.ibikmetov.microservices.auth.controller

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.slf4j.LoggerFactory
import org.springframework.http.*
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.math.BigInteger
import java.security.MessageDigest

@RestController
class ControllerV1 {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    fun String.toMD5(): String {
        val bytes = MessageDigest.getInstance("MD5").digest(this.toByteArray())
        return bytes.toHex()
    }

    fun ByteArray.toHex(): String {
        return joinToString("") { "%02x".format(it) }
    }

    fun getToken() = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJncm91cHMiOlsidXNlcnMiXSwibmFtZSI6Im5hbWUifQ.Syd14kWRdL4ry09cQBCz_l-RfjJkyA8IRzI6bsazjUQ"

    @GetMapping("health")
    fun health1(@RequestHeader("x-username") header: String?): String? {
        log.info("header: $header")
        return "{\"status1\": \"OK\"}"
    }

    @PostMapping("health")
    fun health2(@RequestHeader("x-username") header: String?): String? {
        log.info("header: $header")
        return "{\"status2\": \"OK\"}"
    }

    @GetMapping("config")
    fun config(): String? {
        return System.getenv().entries.joinToString("\n")
    }

    @GetMapping("/auth/logout")
    fun logout(@RequestHeader("Authorization") encoding: String?): ResponseEntity<String> {
        return ResponseEntity<String>("Logout", HttpStatus.OK)
    }

    @GetMapping("/auth/login")
    @ResponseBody
    fun getLogin(request: HttpServletRequest): String {
        log.info("/login - get")
        val state = request.getParameter("state")
        return state
    }

    @PostMapping("/auth/login")
    fun postLogin(request: HttpServletRequest): ResponseEntity<String> {
        log.info("/login - post")
        val username: String? = request.getParameter("username")
        val state: String? = request.getParameter("state")
        log.info("username: $username")
        log.info("state: $state")
        val httpHeaders = HttpHeaders()

        if (username !== null) {
            username.toMD5()
        }


        val response = if (state === null) {
            ResponseEntity<String>("Logged in", httpHeaders, HttpStatus.OK)
        } else {
            val location = "http://localhost:8000/health"
            httpHeaders.location = URI(location)
            ResponseEntity<String>(httpHeaders, HttpStatus.FOUND)
        }
        return response
    }

    @RequestMapping(value = ["/auth/istio/*"])
    fun auth(request: HttpServletRequest,
        ): ResponseEntity<String> {
        log.info("/auth/istio")

        val location = "http://localhost:8000/auth/login?state=from_auth"
        log.info("location: $location")
        val httpHeaders = HttpHeaders()
        httpHeaders.location = URI(location)

        return ResponseEntity<String>(httpHeaders, HttpStatus.FOUND)
    }

}