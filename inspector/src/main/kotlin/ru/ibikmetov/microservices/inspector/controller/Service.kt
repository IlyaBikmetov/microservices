package ru.ibikmetov.microservices.inspector.controller

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.ibikmetov.microservices.inspector.api.v1.*
import ru.ibikmetov.microservices.inspector.data.ClaimRepository
import ru.ibikmetov.microservices.inspector.model.ClaimDB
import ru.ibikmetov.microservices.parking.model.Claim
import java.time.LocalDateTime

@Service
class InspectorService(val repo: ClaimRepository) {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    fun claim(claim: Claim?): Result {
        val res: Result = try {
            if (!claim?.username.isNullOrEmpty()) {
                if (!claim?.requestKey.isNullOrEmpty()) {
                    val o = repo.findByRequestKey(claim?.requestKey!!)
                    if (!o.isEmpty) {
                        if (o.get().place == claim.place) {
                            Result("0", "Success")
                        } else {
                            Result("1", "Idempotent Parameter Mismatch")
                        }
                    } else {
                        repo.save(ClaimDB(
                            null,
                            claim.place,
                            claim.requestKey,
                            LocalDateTime.now().withNano(0)
                        ))
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
        return res
    }
    fun claim(request: ClaimRequest, username: String?, requestKey: String?): Result {
        return this.claim(Claim(
            request.claim?.place,
            username,
            requestKey,
            )
        )
    }
}