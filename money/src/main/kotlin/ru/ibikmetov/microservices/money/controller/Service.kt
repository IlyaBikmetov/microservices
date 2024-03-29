package ru.ibikmetov.microservices.money.controller

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import ru.ibikmetov.microservices.money.api.v1.*
import ru.ibikmetov.microservices.money.data.MoneyRepository
import ru.ibikmetov.microservices.money.model.Money
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Optional

@Service
class MoneyService(var repo: MoneyRepository) {
    private val log = LoggerFactory.getLogger(ControllerV1::class.java)

    fun check(request: CheckRequest, username: String?): MoneyResponse {
        log.info("read: $username")
        val result: MoneyResponse = try {
            val o = repo.findByUsername(request.money?.username!!)
            val moneyResult = if (!o.isEmpty) {
                val money = o.get()
                if (money.money?.compareTo(BigDecimal.valueOf(0.0))!! == 1) {
                    "MoneyExists"
                } else {
                    "MoneyNotExists"
                }
            } else {
                "Account not found"
            }
            MoneyResponse("0", moneyResult)
        } catch (e: Exception) {
            MoneyResponse("1", e.message)
        }
        log.info("result: $result")
        return result
    }

    fun operation(request: OperationRequest, username: String?, requestKey: String?): MoneyResponse {
        log.info("start - username: $username - requestKey: $requestKey")
        val result: MoneyResponse = try {
            if (!username.isNullOrEmpty()) {
                if (!requestKey.isNullOrEmpty()) {
                    val o = if (request.money?.operation == Operation.CANCELLED) {
                        repo.findByUsernameAndLastOperationAndRequestKey(username, request.money.operation.toString(), requestKey)
                    } else {
                        repo.findByUsernameAndLastOperationAndLastValueAndRequestKey(username, request.money?.operation.toString(), request.money?.money.toString(), requestKey)
                    }
                    var moneyResult: String? = null
                    if (!o.isEmpty) {
                        moneyResult = "Operation success"
                    } else {
                        val m = repo.findByUsername(username)
                        if (!m.isEmpty) {
                            val money = m.get()
                            if (request.money?.operation == Operation.ADD) {
                                money.money = money.money?.plus(request.money.money!!)
                                money.lastOperation = request.money.operation.toString()
                                money.lastValue = request.money.money
                                money.requestKey = requestKey
                                money.updated = LocalDateTime.now()
                                repo.save(money)
                                moneyResult = "Operation success"
                            }
                            if (request.money?.operation == Operation.SUBSTR) {
                                money.money = money.money?.minus(request.money.money!!)
                                money.lastOperation = request.money.operation.toString()
                                money.lastValue = request.money.money
                                money.requestKey = requestKey
                                money.updated = LocalDateTime.now()
                                repo.save(money)
                                moneyResult = "Operation success"
                            }
                            if (request.money?.operation == Operation.HOLD) {
                                if (money.money!!.compareTo(request.money.money!!) == 1) {
                                    money.money = money.money?.minus(request.money.money)
                                    money.holdMoney = request.money.money
                                    money.lastOperation = request.money.operation.toString()
                                    money.lastValue = request.money.money
                                    money.requestKey = requestKey
                                    money.updated = LocalDateTime.now()
                                    repo.save(money)
                                    moneyResult = "Operation success"
                                } else {
                                    moneyResult = "Money is tight"
                                }
                            }
                            if (request.money?.operation == Operation.CANCELLED) {
                                if (money.holdMoney != null) {
                                    log.info("CANCELLED")
                                    money.money = money.money?.plus(money.holdMoney!!)
                                    money.holdMoney = null
                                    money.lastOperation = request.money.operation.toString()
                                    money.lastValue = null
                                    money.requestKey = requestKey
                                    money.updated = LocalDateTime.now()
                                    repo.save(money)
                                    moneyResult = "Operation success"
                                } else {
                                    moneyResult = "Money is not hold"
                                }
                            }
                        } else {
                            val money = Money()
                            if (request.money?.operation == Operation.ADD) {
                                money.username = username
                                money.money = request.money.money
                                money.lastOperation = request.money.operation.toString()
                                money.lastValue = request.money.money
                                money.requestKey = requestKey
                                money.updated = LocalDateTime.now()
                                repo.save(money)
                                moneyResult = "Operation success"
                            }
                            if (request.money?.operation == Operation.SUBSTR ||
                                request.money?.operation == Operation.HOLD) {
                                moneyResult = "Account not found"
                            }
                        }
                    }
                    MoneyResponse("0", moneyResult)
                } else {
                    MoneyResponse("1", "Request key not find")
                }
            } else {
                MoneyResponse("1", "Not authorized")
            }
        } catch (e: Exception) {
            MoneyResponse("1", e.message)
        }
        log.info("result: $result")
        return result
    }
}