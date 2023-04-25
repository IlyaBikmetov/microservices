package ru.ibikmetov.microservices.orders.controller

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.ibikmetov.microservices.orders.api.v1.*
import ru.ibikmetov.microservices.orders.data.OrderRepository
import ru.ibikmetov.microservices.orders.data.OrdersVersionRepository
import ru.ibikmetov.microservices.orders.model.Order
import ru.ibikmetov.microservices.orders.model.OrdersVersion
import java.time.LocalDateTime

@Service
class OrderService(var repo: OrderRepository, var version: OrdersVersionRepository) {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    fun updateVersion(username: String?) {
        var v = version.findByUsername(username)
        if (v.isEmpty()) {
            version.save(OrdersVersion(null, username, 1))
        } else {
            v[0].version = v[0].version!! + 1
            version.save(v[0])
        }
    }

    fun create(request: OrderCreateUpdateRequest, username: String?, requestKey: String?): OrderResponse {
        log.info("create - username: $username - requestKey: $requestKey")
        var uObject: OrderObject? = null
        val result: Result = try {
            if (!username.isNullOrEmpty()) {
                if (!requestKey.isNullOrEmpty()) {
                    val o = repo.findByUsernameAndRequestKey(username, requestKey)
                    if (!o.isEmpty) {
                        if (o.get().orderName == request.order?.orderName
                            && o.get().quantity == request.order?.quantity
                            && o.get().deleted == null) {
                            uObject = OrderObject(
                                o.get().id,
                                o.get().orderName,
                                o.get().quantity,
                            )
                            Result("0", "Success")
                        } else {
                            Result("1", "Idempotent Parameter Mismatch")
                        }
                    } else {
                        val order = Order(
                            null,
                            username,
                            request.order?.orderName,
                            request.order?.quantity,
                            requestKey,
                            null,
                        )
                        repo.save(order)
                        updateVersion(username)
                        uObject = OrderObject(
                            order.id,
                            order.orderName,
                            order.quantity,
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
        return OrderResponse(result, uObject)
    }

    fun update(request: OrderCreateUpdateRequest, username: String?): OrderResponse {
        log.info("update: $username")
        val result: Result = try {
            val id = request.order?.id
            val o = repo.findByIdAndDeleted(id.toString(), null)
            if (!o.isEmpty) {
                val order = o.get()
                if (username == "admin" || username == order.username) {
                    val orderUpdate = Order(
                        request.order?.id,
                        username,
                        request.order?.orderName,
                        request.order?.quantity,
                        order.requestKey,
                        null,
                    )
                    repo.save(orderUpdate)
                    updateVersion(username)
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
        return OrderResponse(result, null)
    }

    fun read(request: OrderReadDeleteRequest, username: String?): OrderResponse {
        log.info("read: $username")
        var uObject: OrderObject? = null
        val result: Result = try {
            val id = request.orderId?.id
            val o = repo.findByIdAndDeleted(id.toString(), null)
            if (!o.isEmpty) {
                val order = o.get()
                if (username == "admin" || username == order.username) {
                    uObject = OrderObject(
                        order.id,
                        order.orderName,
                        order.quantity,
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
        return OrderResponse(result, uObject)
    }

    fun delete(request: OrderReadDeleteRequest, username: String?): OrderResponse {
        log.info("delete: $username")
        val result: Result = try {
            val id = request.orderId?.id
            val o = repo.findById(id.toString())
            if (!o.isEmpty) {
                val order = o.get()
                if (username == "admin" || username == order.username) {
                    if (order.deleted == null) {
                        order.deleted = LocalDateTime.now()
                        repo.save(order)
                        updateVersion(username)
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
        return OrderResponse(result, null)
    }

    fun orders(request: OrdersRequest, username: String?): OrdersResponse {
        log.info("orders: $username")
        var uObject: OrdersList? = null
        val result: Result = try {
            if (!username.isNullOrEmpty()) {
                uObject = OrdersList(repo.findByUsernameAndDeleted(request.userName?.username, null).map { it.toOrderObject() }  )
                Result("0", "Success")
            } else {
                Result("1", "Not authorized")
            }
        } catch (e: Exception) {
            Result("1", e.message)
        }
        log.info("result: $result")
        return OrdersResponse(result, uObject)
    }
    fun Order.toOrderObject() = OrderObject(this.id, this.orderName, this.quantity)
}