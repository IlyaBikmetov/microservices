package ru.ibikmetov.microservices.parking.controller

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import ru.ibikmetov.microservices.parking.api.v1.*
import ru.ibikmetov.microservices.parking.data.ParkingRepository
import ru.ibikmetov.microservices.parking.model.Claim
import ru.ibikmetov.microservices.parking.model.Parking
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit


@Service
class ParkingService(val repo: ParkingRepository
                    , val kafka: KafkaTemplate<String, Claim>?
                    , val microservicesConfig: MicroservicesConfig) {

    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    fun rest(method: String, username: String?, requestKey: String?, content: String, type: String): String {
        val url = URL(method)
        val input = content.toByteArray(Charsets.UTF_8)
        val text = StringBuffer()
        try {
            with(url.openConnection() as HttpURLConnection) {
                requestMethod = type
                addRequestProperty("x-username", username)
                addRequestProperty("x-request-key", requestKey)
                setRequestProperty("Content-Type", "application/json")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent","Mozilla/5.0 ( compatible ) ");
                doOutput = true
                outputStream.write(input)
                inputStream.bufferedReader().use {
                    it.lines().forEach { line ->
                        text.append(line)
                        log.info(line)
                    }
                }
            }
        } catch (e: Exception) {
            log.error(e.printStackTrace().toString())
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
                        val placeContent = "{\"place\":{\"placeId\": ${request.parking?.placeId}}}"
                        val response = rest(microservicesConfig.placesURL + "/api/v1/read", username, requestKey, placeContent, "POST")
                        val place = ObjectMapper().readValue(response, PlaceResponse::class.java).place

                        val parking = o.get()
                        if (o.get().placeId == request.parking?.placeId
                            && o.get().numberVehicle == request.parking?.numberVehicle
                            && place?.status == request.parking?.status
                        ) {
                            uObject = ParkingObject(
                                parking.id,
                                parking.username,
                                parking.numberVehicle,
                                place?.place,
                                parking.start?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                                parking.stop?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                                place?.status.toString(),
                                parking.money,
                            )
                            Result("0", "Success")
                        } else {
                            Result("1", "Idempotent Parameter Mismatch")
                        }
                    } else {
                        val parking: Parking
                        var place: PlaceObject?
                        var placeContent: String?
                        val placeResponse: PlaceResponse?
                        var response: String?
                        val resultMessage: String?

                        if (request.parking?.id != null) {
                            parking = repo.findById(request.parking.id.toString()).get()

                            placeContent = "{\"place\":{\"placeId\": ${parking.placeId}}}"
                            response = rest(microservicesConfig.placesURL + "/api/v1/read", username, requestKey, placeContent, "POST")
                            placeResponse = ObjectMapper().readValue(response, PlaceResponse::class.java)
                            place = placeResponse.place

                            if ((place?.status == ParkingStatus.BOOKING && request.parking.status == ParkingStatus.PARKING) ||
                                (place?.status == ParkingStatus.FREE && request.parking.status != ParkingStatus.FREE)) {

                                placeContent = "{\"place\":{\"placeId\": ${parking.placeId}, \"status\": \"${request.parking.status}\"}}"
                                response = rest(microservicesConfig.placesURL + "/api/v1/update", username, requestKey, placeContent, "POST")
                                place = ObjectMapper().readValue(response, PlaceResponse::class.java).place

                                parking.requestKey = requestKey
                                parking.start = LocalDateTime.now().withNano(0)
                                parking.updated = LocalDateTime.now().withNano(0)
                                repo.save(parking)
                            }
                            uObject = ParkingObject(
                                parking.id,
                                parking.username,
                                parking.numberVehicle,
                                place?.place,
                                parking.start?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                                parking.stop?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                                place?.status.toString(),
                                parking.money,
                            )
                            resultMessage = "Success"
                        } else {
                            //--Бронируем-место--
                            placeContent = "{\"place\":{\"placeId\": ${request.parking?.placeId}, \"status\": \"${request.parking?.status}\"}}"
                            response = rest(microservicesConfig.placesURL + "/api/v1/update", username, requestKey, placeContent, "POST")
                            placeResponse = ObjectMapper().readValue(response, PlaceResponse::class.java)
                            place = placeResponse.place

                            if (placeResponse.result?.code.equals("0") && placeResponse.result?.message.equals("Success")) {
                                ObjectMapper().readValue(response, PlaceResponse::class.java)

                                //--Холдим-деньги--
                                val moneyContent = "{\"money\":{\"username\": \"${username}\", \"money\": 100, \"operation\": \"hold\"}}"
                                val moneyResponse = rest(microservicesConfig.moneyURL + "/api/v1/operation", username, requestKey, moneyContent, "POST")
                                val moneyResult = ObjectMapper().readValue(moneyResponse, MoneyResponse::class.java)

                                if (moneyResult.code.equals("0") && moneyResult.message.equals("Operation success")) {
                                    parking = Parking()
                                    parking.username = username
                                    parking.numberVehicle = request.parking?.numberVehicle
                                    parking.requestKey = requestKey
                                    parking.placeId = request.parking?.placeId
                                    parking.start = LocalDateTime.now().withNano(0)
                                    parking.updated = LocalDateTime.now().withNano(0)
                                    repo.save(parking)
                                    uObject = ParkingObject(
                                        parking.id,
                                        parking.username,
                                        parking.numberVehicle,
                                        place?.place,
                                        parking.start?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                                        parking.stop?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                                        place?.status.toString(),
                                        parking.money,
                                    )
                                    resultMessage = "Success"
                                } else {
                                    //--Разбронируем-место--
                                    placeContent = "{\"place\":{\"placeId\": ${request.parking?.placeId}, \"status\": \"free\"}}"
                                    response = rest(microservicesConfig.placesURL + "/api/v1/update", username, requestKey, placeContent, "POST")
                                    ObjectMapper().readValue(response, PlaceResponse::class.java)
                                    resultMessage = moneyResult.message
                                }
                            } else {
                                resultMessage = placeResponse.result?.message
                            }
                        }
                        Result("0", resultMessage)
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

                 val placeContent = "{\"place\":{\"placeId\": ${parking.placeId}}}"
                 val response = rest(microservicesConfig.placesURL + "/api/v1/read", username, null, placeContent, "POST")
                 val placeResponse: PlaceResponse = ObjectMapper().readValue(response, PlaceResponse::class.java)
                 val place = placeResponse.place

                 if (username == "admin" || username == parking.username) {
                     uObject = ParkingObject(
                         parking.id,
                         parking.username,
                         parking.numberVehicle,
                         place?.place,
                         parking.start?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                         parking.stop?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                         place?.status.toString(),
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

                    var placeContent = "{\"place\":{\"placeId\": ${parking.placeId}}}"
                    val response = rest(microservicesConfig.placesURL + "/api/v1/read", username, null, placeContent, "POST")
                    val place = ObjectMapper().readValue(response, PlaceResponse::class.java).place

                    if (place?.status != ParkingStatus.FREE) {
                        parking.stop = LocalDateTime.now().withNano(0)
                        parking.updated = LocalDateTime.now().withNano(0)
                        //--------
                        var moneyContent = "{\"money\":{\"username\": \"${username}\", \"operation\": \"cancelled\"}}"
                        var moneyResponse = rest(microservicesConfig.moneyURL + "/api/v1/operation", username, requestKey, moneyContent, "POST")
                        ObjectMapper().readValue(moneyResponse, MoneyResponse::class.java)

                        val money = BigDecimal(ChronoUnit.MINUTES.between(parking.start, parking.stop) * 10.1 + 100)
                            .setScale(2, RoundingMode.HALF_EVEN)
                         moneyContent = "{\"money\":{\"username\": \"${username}\", \"money\": ${money}, \"operation\": \"substr\"}}"
                        moneyResponse = rest(microservicesConfig.moneyURL + "/api/v1/operation", username, requestKey, moneyContent, "POST")
                        val moneyResult = ObjectMapper().readValue(moneyResponse, MoneyResponse::class.java)

                        if (moneyResult.code.equals("0")) {
                            parking.money = money
                        }
                        repo.save(parking)
                        //--------
                        placeContent = "{\"place\":{\"placeId\": ${place?.id}, \"status\": \"free\"}}"
                        val responseResult = rest(microservicesConfig.placesURL + "/api/v1/update", username, requestKey, placeContent, "POST")
                        ObjectMapper().readValue(responseResult, PlaceResponse::class.java).place
                        //--------
                        val dwhContent = "{\"parking\":{\"id\": \"${parking.id}\"," +
                                "\"username\": \"${parking.username}\"," +
                                "\"numberVehicle\": \"${parking.numberVehicle}\"," +
                                "\"place\": \"${place?.place}\"," +
                                "\"start\": \"${parking.start?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))}\"," +
                                "\"stop\": \"${parking.stop?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss"))}\"," +
                                "\"money\": \"${parking.money}\"}}"
                        val dwhResponse = rest(microservicesConfig.dwhURL + "/api/v1/put", username, requestKey, dwhContent, "POST")
                        val dwhResult = ObjectMapper().readValue(dwhResponse, Result::class.java)

                        if (dwhResult.code.equals("0")) {
                            repo.deleteById(parking.id.toString())
                        }
                        //--------
                        uObject = ParkingObject(
                            parking.id,
                            parking.username,
                            parking.numberVehicle,
                            place?.place,
                            parking.start?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                            parking.stop?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")),
                            place?.status.toString(),
                            parking.money
                        )
                    }
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

    fun claim(request: ClaimRequest, username: String?, requestKey: String?): Result {
        val res: Result = try {
            if (!username.isNullOrEmpty()) {
                if (!requestKey.isNullOrEmpty()) {
                    kafka?.send("parking_claims", Claim(
                        request.claim?.place,
                        username,
                        requestKey,
                        )
                    )
                    Result("0", "Success")
                } else {
                    Result("1", "Request key not find")
                }
            } else {
                Result("1", "Not authorized")
            }
        } catch (e: Exception) {
            Result("1", e.message)
        }
        return res
    }
}