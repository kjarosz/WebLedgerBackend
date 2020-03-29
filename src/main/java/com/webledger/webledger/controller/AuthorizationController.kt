package com.webledger.webledger.controller

import com.webledger.webledger.service.AuthorizationService
import io.swagger.annotations.ApiOperation
import org.apache.logging.log4j.kotlin.Logging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@RestController
@CrossOrigin(origins = ["http://localhost:4200", "https://webledger.ddns.net/"])
@RequestMapping("accounts")
class AuthorizationController(
        @Autowired
        private val authorizationService: AuthorizationService
) : Logging {
    @ApiOperation(value = "Log in user with username and password", response = Void::class)
    @PostMapping
    fun login(@RequestBody username: String,
              @RequestBody password: String,
              response: HttpServletResponse): ResponseEntity<Void>
    {
        val webledgerSession = authorizationService.login(username, password)
        val sessionCookie = Cookie("sessionId", webledgerSession.sessionId.toString())
        response.addCookie(sessionCookie)
        return ResponseEntity.ok().build()
    }
}
