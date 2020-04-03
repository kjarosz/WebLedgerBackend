package com.webledger.webledger.controller

import com.webledger.webledger.service.UserService
import com.webledger.webledger.transferobject.UserTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.util.UriComponentsBuilder
import javax.servlet.ServletContext

@RestController
@RequestMapping("/user")
class UserController(
        @Autowired
        private val userService: UserService,
        @Autowired
        private val servletContext: ServletContext
) {
    @PostMapping("/register")
    fun register(@RequestBody userTo: UserTo): ResponseEntity<Void> {
        userService.register(userTo)
        val newUserUri = UriComponentsBuilder
                .newInstance()
                .pathSegment(servletContext.contextPath, "/user", "/{username}")
                .buildAndExpand(userTo.username)
        return ResponseEntity.created(newUserUri.toUri()).build()
    }
}
