package ru.ibikmetov.microservices.places.controller

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.ibikmetov.microservices.places.model.Place
import ru.ibikmetov.microservices.places.data.PlaceRepository
import ru.ibikmetov.microservices.places.api.v1.*

@Service
class PlaceService(var repo: PlaceRepository) {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    fun read(request: PlaceReadRequest, username: String?): PlaceResponse {
        log.info("read: $username")
        var uObject: PlaceObject? = null
        val result: Result = try {
            if (!username.isNullOrEmpty()) {
                val o = repo.findById(request.place?.placeId.toString())
                if (!o.isEmpty) {
                    val place = o.get()
                    uObject = PlaceObject(
                        place.id,
                        place.place,
                        ParkingStatus.values()[place.statusId?.toInt()!!],
                    )
                    Result("0", "Success")
                } else {
                    Result("1", "Not found")
                }
            } else {
                Result("1", "Not authorized")
            }
         } catch (e: Exception) {
             Result("1", e.message)
         }
         log.info("result: $result")
         return PlaceResponse(result, uObject)
    }

    fun update(request: PlaceUpdateRequest, username: String?, requestKey: String?): PlaceResponse {
        log.info("update: $username")
        var uObject: PlaceObject? = null
        val result: Result = try {
            if (!username.isNullOrEmpty()) {
                if (!requestKey.isNullOrEmpty()) {
                    val o = repo.findById(request.place?.placeId.toString())
                    if (!o.isEmpty) {
                        val place = o.get()
                        if (place.statusId?.toInt() != request.place?.status?.ordinal) {
                            log.info("update: ${request.place?.status}")
                            place.statusId = request.place?.status?.ordinal?.toLong()
                            repo.save(place)
                        }
                        uObject = PlaceObject(
                            place.id,
                            place.place,
                            ParkingStatus.values()[place.statusId?.toInt()!!],
                        )
                        Result("0", "Success")
                    } else {
                        Result("1", "Not found")
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
        return PlaceResponse(result, uObject)
    }

    fun free(username: String?): PlacesResponse {
        log.info("free")
        var uObject: List<PlaceObject>? = null
        val result: Result = try {
            if (!username.isNullOrEmpty()) {
                uObject = repo.findByStatusId(0).map { it.toPlaceObject() }
                Result("0", "Success")
            } else {
                Result("1", "Not authorized")
            }
        } catch (e: Exception) {
            Result("1", e.message)
        }
        log.info("result: $result")
        return PlacesResponse(result, uObject)
    }
    fun Place.toPlaceObject() = PlaceObject(
        this.id,
        this.place,
        ParkingStatus.values()[this.statusId?.toInt()!!],
    )
}