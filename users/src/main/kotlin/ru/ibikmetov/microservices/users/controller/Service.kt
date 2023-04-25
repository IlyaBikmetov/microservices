package ru.ibikmetov.microservices.users.controller

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.ibikmetov.microservices.users.api.v1.*
import ru.ibikmetov.microservices.users.data.UserRepository
import ru.ibikmetov.microservices.users.model.User

@Service
class UserService(var repo: UserRepository) {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    fun create(request: UserCreateUpdateRequest): UserResponse {
        log.info("create")
        var uObject: UserObject? = null
        val result: Result =  try {
            val userCreate = User(
                request.user?.id,
                request.user?.username,
                request.user?.firstName,
                request.user?.lastName,
                request.user?.email,
                request.user?.phone,
            )
            val o = repo.findByUsername(request.user?.username)
            if (o.isEmpty()) {
                repo.save(userCreate)
                uObject = UserObject(
                    userCreate.id,
                    userCreate.username,
                    userCreate.firstName,
                    userCreate.lastName,
                    userCreate.email,
                    userCreate.phone,
                )
                Result("0", "Success")
            } else {
                Result("1", "User exist")
            }
        } catch (e: Exception) {
            Result("1", e.message)
        }
        log.info("result: $result")
        return UserResponse(result, uObject)
    }

    fun update(request: UserCreateUpdateRequest, username: String?): UserResponse {
        log.info("update: $username")
        val result: Result = try {
            val id = request.user?.id
            val o = repo.findById(id.toString())
            if (!o.isEmpty) {
                val user = o.get()
                if (username == "admin" || username == user.username) {
                    val userUpdate = User(
                        request.user?.id,
                        user.username,
                        request.user?.firstName,
                        request.user?.lastName,
                        request.user?.email,
                        request.user?.phone,
                    )
                    repo.save(userUpdate)
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
        return UserResponse(result, null)
    }

    fun read(request: UserReadDeleteRequest, username: String?): UserResponse {
        log.info("read: $username")
        var uObject: UserObject? = null
        val result: Result = try {
            val id = request.userId?.id
            val o = repo.findById(id.toString())
            if (!o.isEmpty) {
                val user = o.get()
                if (username == "admin" || username == user.username) {
                    uObject = UserObject(
                        user.id,
                        user.username,
                        user.firstName,
                        user.lastName,
                        user.email,
                        user.phone,
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
        return UserResponse(result, uObject)
    }

    fun delete(request: UserReadDeleteRequest, username: String?): UserResponse {
        log.info("delete: $username")
        val result: Result = try {
            val id = request.userId?.id
            val o = repo.findById(id.toString())
            if (!o.isEmpty) {
                val user = o.get()
                if (username == "admin" || username == user.username) {
                    repo.deleteById(id.toString())
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
        return UserResponse(result, null)
    }
}