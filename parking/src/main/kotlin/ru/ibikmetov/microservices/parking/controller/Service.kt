package ru.ibikmetov.microservices.parking.controller

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.ibikmetov.microservices.parking.api.v1.*
import ru.ibikmetov.microservices.parking.data.ParkingPlaceRepository
import ru.ibikmetov.microservices.parking.data.ParkingRepository
import ru.ibikmetov.microservices.parking.model.Parking
import ru.ibikmetov.microservices.parking.model.ParkingPlace
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.net.*

@Service
class ParkingService(var repo: ParkingRepository, var repoPlace: ParkingPlaceRepository, var microservicesConfig: MicroservicesConfig) {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    fun rest(method: String, username: String?, requestKey: String?, content: String): String {
        val url = URL(method)
        val input = content.toByteArray(Charsets.UTF_8)
        val text = StringBuffer()
        with (url.openConnection() as HttpURLConnection) {
            requestMethod = "POST"
            addRequestProperty("x-username", username)
            addRequestProperty("x-request-key", requestKey)
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("Accept", "application/json")
            doOutput = true
            outputStream.write(input)
            inputStream.bufferedReader().use {
                it.lines().forEach { line ->
                    text.append(line)
                    log.info(line)
                }
            }
        }
        return text.toString()
    }

    fun start(request: StartRequest, username: String?, requestKey: String?): ParkingResponse {
        log.info("start - username: $username - requestKey: $requestKey")
        var uObject: ParkingObject? = null
        val result: Result = try {
            if (!username.isNullOrEmpty()) {
                if (!requestKey.isNullOrEmpty()) {
                    val o = repo.findByUsernameAndRequestKey(username, requestKey)
                    if (!o.isEmpty) {
                        val p = repoPlace.findById(o.get().placeId.toString())
                        if (o.get().placeId == request.parking?.placeId
                            && p.get().statusId?.toInt() == request.parking?.status?.ordinal
                        ) {
                            uObject = ParkingObject(
                                o.get().id,
                                o.get().username,
                                p.get().place,
                                o.get().start?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                                o.get().stop?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                                p.get().place,
                                o.get().money,
                            )
                            Result("0", "Success")
                        } else {
                            Result("1", "Idempotent Parameter Mismatch")
                        }
                    } else {
                        val parking: Parking
                        val place: ParkingPlace
                        if (request.parking?.id != null) {
                            parking = repo.findById(request.parking.id.toString()).get()
                            place = repoPlace.findById(parking.placeId.toString()).get()
                            if ((place.statusId?.toInt() == ParkingStatus.BOOKING.ordinal && request.parking.status == ParkingStatus.PARKING) ||
                                (place.statusId?.toInt() == ParkingStatus.FREE.ordinal && request.parking.status != ParkingStatus.FREE)) {
                                parking.start = LocalDateTime.now().withNano(0)
                                parking.updated = LocalDateTime.now().withNano(0)
                                repo.save(parking)
                                place.statusId = request.parking.status?.ordinal?.toLong()
                                repoPlace.save(place)
                            }
                        } else {
                            parking = Parking()
                            parking.username = username
                            parking.requestKey = requestKey
                            parking.placeId = request.parking?.placeId
                            parking.start = LocalDateTime.now().withNano(0)
                            parking.updated = LocalDateTime.now().withNano(0)
                            repo.save(parking)
                            place = repoPlace.findById(parking.placeId.toString()).get()
                            place.statusId = request.parking?.status?.ordinal?.toLong()
                            repoPlace.save(place)
                        }
                        uObject = ParkingObject(
                            parking.id,
                            parking.username,
                            place.place,
                            parking.start?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                            parking.stop?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                            ParkingStatus.values()[place.statusId?.toInt()!!].toString(),
                            parking.money,
                        )
                        Result("0", "Success")
                    }
                } else {
                    Result("1", "Request key not find")
                }
            } else {
                Result("1", "Not authorized")
            }
        } catch (e: Exception) {
            Result("1", e.message)
        }
        log.info("result: $result")
        return ParkingResponse(result, uObject)
    }

     fun read(request: ParkingId, username: String?): ParkingResponse {
         log.info("read: $username")
         var uObject: ParkingObject? = null
         val result: Result = try {
             val o = repo.findById(request.parking?.id.toString())
             if (!o.isEmpty) {
                 val parking = o.get()
                 val p = repoPlace.findById(o.get().placeId.toString())
                 if (username == "admin" || username == parking.username) {
                     uObject = ParkingObject(
                         parking.id,
                         parking.username,
                         p.get().place,
                         parking.start?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                         parking.stop?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                         ParkingStatus.values()[p.get().statusId?.toInt()!!].toString(),
                         parking.money
                     )
                     Result("0", "Success")
                 } else {
                     Result("1", "Not authorized")
                 }
             } else {
                 Result("1", "Not found")
             }
         } catch (e: Exception) {
             Result("1", e.message)
         }
         log.info("result: $result")
         return ParkingResponse(result, uObject)
    }

    fun stop(request: StopRequest, username: String?, requestKey: String?): ParkingResponse {
        log.info("stop: $username")
        var uObject: ParkingObject? = null
        val result: Result = try {
            val o = repo.findById(request.parking?.id.toString())
            if (!o.isEmpty) {
                val parking = o.get()
                if (username == "admin" || username == parking.username) {
                    val p = repoPlace.findById(o.get().placeId.toString())
                    if (p.get().statusId?.toInt() != ParkingStatus.FREE.ordinal) {
                        log.info("stop1: $username")
                        parking.stop = LocalDateTime.now().withNano(0)
                        parking.updated = LocalDateTime.now().withNano(0)

                        val money = BigDecimal(ChronoUnit.MINUTES.between(parking.start, parking.stop) * 10.1).setScale(2, BigDecimal.ROUND_DOWN)
                        log.info("stop2: $money")
                        val moneyContent = "{\"money\":{\"username\": \"${username}\", \"money\": ${money}, \"operation\": \"substr\"}}"
                        val moneyResponse = rest(microservicesConfig.moneyURL + "/api/v1/money/operation", username, requestKey, moneyContent)

                        var moneyResult = false
                        if (moneyResponse.indexOf("Success") > -1 && moneyResponse.indexOf("Operation success") > -1) {
                            moneyResult = true
                        }
                        if (moneyResult) {
                            parking.money = money
                        }
                        repo.save(parking)

                        val dwhContent = "{\"parking\":{\"id\": \"${parking.id}\"," +
                                "\"username\": \"${parking.username}\"," +
                                "\"place\": \"${p.get().place}\"," +
                                "\"start\": \"${parking.start?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))}\"," +
                                "\"stop\": \"${parking.stop?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))}\"," +
                                "\"money\": \"${parking.money}\"}}"
                        val dwhResponse = rest(microservicesConfig.dwhURL + "/api/v1/dwh/put", username, requestKey, dwhContent)
                        log.info("dwhResponse: $dwhResponse")
                        var dwhResult = false
                        if (dwhResponse.indexOf("Success") > -1) {
                            dwhResult = true
                        }

                        if (dwhResult) {
                            repo.deleteById(parking.id.toString())
                        }
                        uObject = ParkingObject(
                            parking.id,
                            parking.username,
                            p.get().place,
                            parking.start?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                            parking.stop?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                            ParkingStatus.values()[p.get().statusId?.toInt()!!].toString(),
                            parking.money
                        )
                        p.get().statusId = ParkingStatus.FREE.ordinal.toLong()
                        repoPlace.save(p.get())
                        Result("0", moneyResponse)
                    } else {
                        Result("0", "Success")
                    }
                } else {
                    Result("1", "Not authorized")
                }
            } else {
                Result("1", "Not found")
            }
        } catch (e: Exception) {
            Result("1", e.message)
        }
        log.info("result: $result")
        return ParkingResponse(result, uObject)
    }

    fun freeparkings(username: String?): ParkingsResponse {
        log.info("parkings: $username")
        var uObject: ParkingsPlaceObjectList? = null
        val result: Result = try {
            if (!username.isNullOrEmpty()) {
                uObject = ParkingsPlaceObjectList(repoPlace.findByStatusId(0).map { it.toParkingPlaceObject() })
                Result("0", "Success")
            } else {
                Result("1", "Not authorized")
            }
        } catch (e: Exception) {
            Result("1", e.message)
        }
        log.info("result: $result")
        return ParkingsResponse(result, uObject)
    }
    fun ParkingPlace.toParkingPlaceObject() = ParkingPlaceObject(
        this.id,
        this.place,
        ParkingStatus.values()[this.statusId?.toInt()!!],
    )
}