package ru.ibikmetov.microservices.controller

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.ibikmetov.api.v1.*
import ru.ibikmetov.microservices.data.UserRepository
import ru.ibikmetov.microservices.model.User

@Service
class UserService(var repo: UserRepository) {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    fun create(request: UserCreateUpdateRequest): UserResponse {
        return try {
            val user = User(
                request.user?.id,
                request.user?.username,
                request.user?.firstName,
                request.user?.lastName,
                request.user?.email,
                request.user?.phone,
            )
            repo.save(user)
            val uObject = UserObject(
                user.id,
                user.username,
                user.firstName,
                user.lastName,
                user.email,
                user.phone,
            )
            UserResponse(Result("0", "Success"), uObject)
        } catch (e: Exception) {
            UserResponse(Result("1", e.message), null)
        }
    }

    fun update(request: UserCreateUpdateRequest): UserResponse {
        val id = request.user?.id
        var result: Result
        val user = repo.findById(id.toString())
        if (user != null) {
            if (!user.isEmpty) {
                result = try {
                    val user = User(
                        request.user?.id,
                        request.user?.username,
                        request.user?.firstName,
                        request.user?.lastName,
                        request.user?.email,
                        request.user?.phone,
                    )
                    repo.save(user)
                    Result("0", "Success")
                } catch (e: Exception) {
                    Result("1", e.message)
                }
            } else {
                result = Result("2", "Not found")
            }
        } else {
            result = Result("1", "Id invalid")
        }
        return UserResponse(result, null)
    }

    fun read(request: UserReadDeleteRequest): UserResponse {
        try {
            val id = request.userId?.id
            if (id != null) {
                val o = repo.findById(id.toString())
                if (!o.isEmpty) {
                    val user = o.get()
                    val uObject = UserObject(
                        user.id,
                        user.username,
                        user.firstName,
                        user.lastName,
                        user.email,
                        user.phone,
                    )
                    return UserResponse(Result("0", "Success"), uObject)
                } else {
                    return UserResponse(Result("1", "Not found"), null)
                }
            } else {
                return UserResponse(Result("2", "Id invalid"), null)
            }
        } catch (e: Exception) {
            return UserResponse(Result("3", "Error"), null)
        }
    }

    fun delete(request: UserReadDeleteRequest): UserResponse {
        try {
            val id = request.userId?.id
            if (id != null) {
                val o = repo.findById(id.toString())
                if (!o.isEmpty) {
                    repo.deleteById(id.toString())
                    return UserResponse(Result("0", "Success"), null)
                } else {
                    return UserResponse(Result("1", "Not found"), null)
                }
            } else {
                return UserResponse(Result("2", "Id invalid"), null)
            }
        } catch (e: Exception) {
            return UserResponse(Result("3", "Error"), null)
        }
    }
}