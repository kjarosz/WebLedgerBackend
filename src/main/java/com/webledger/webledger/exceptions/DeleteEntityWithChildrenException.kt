package com.webledger.webledger.exceptions

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
class DeleteEntityWithChildrenException(message: String): Throwable(message)
