package com.webledger.webledger.controller

import com.webledger.webledger.entity.User
import com.webledger.webledger.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.util.UriBuilder
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
    fun register(@RequestBody user: User): ResponseEntity<Void> {
        userService.register(user)
        val newUserUri = UriComponentsBuilder
                .newInstance()
                .pathSegment(servletContext.contextPath, "/user", "/{username}")
                .buildAndExpand(user.username)
        return ResponseEntity.created(newUserUri.toUri()).build()
    }
}
