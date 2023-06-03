package ru.ibikmetov.microservices.dwh.controller

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.ibikmetov.microservices.dwh.api.v1.ParkingDwhRequest
import ru.ibikmetov.microservices.dwh.api.v1.ParkingDwhResponse
import ru.ibikmetov.microservices.dwh.data.ParkingDwhRepository
import ru.ibikmetov.microservices.dwh.model.ParkingDwh
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class ParkingDwhService(var repo: ParkingDwhRepository) {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    fun put(request: ParkingDwhRequest, username: String?, requestKey: String?): ParkingDwhResponse {
        log.info("start - username: $username - requestKey: $requestKey")
        val result: ParkingDwhResponse = try {
            if (!username.isNullOrEmpty()) {
                if (!requestKey.isNullOrEmpty()) {
                    val o = repo.findByUsernameAndRequestKey(username, requestKey)
                    if (!o.isEmpty) {
                        if (o.get().id == request.parking?.id
                            && o.get().username == request.parking?.username
                            && o.get().place == request.parking?.place
                            && o.get().start?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) == request.parking?.start
                            && o.get().stop?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")) == request.parking?.stop
                            && o.get().money == request.parking?.money
                        ) {
                            ParkingDwhResponse("0", "Success")
                        } else {
                            ParkingDwhResponse("1", "Idempotent Parameter Mismatch")
                        }
                    } else {
                        val parking = ParkingDwh()
                        parking.id = request.parking?.id
                        parking.username = request.parking?.username
                        parking.place = request.parking?.place
                        parking.start = LocalDateTime.parse(request.parking?.start, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
                        parking.stop = LocalDateTime.parse(request.parking?.stop, DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))
                        parking.money = BigDecimal.valueOf(request.parking?.money!!.toDouble())
                        parking.requestKey = requestKey
                        repo.save(parking)
                        ParkingDwhResponse("0", "Success")
                    }
                } else {
                    ParkingDwhResponse("1", "Request key not find")
                }
            } else {
                ParkingDwhResponse("1", "Not authorized")
            }
        } catch (e: Exception) {
            ParkingDwhResponse("1", e.message)
        }
        return result
    }
}