package com.webledger.webledger.controller

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class GlobalExceptionHandler {

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    class AccountNotFoundException(message: String?) : Throwable(message)
}