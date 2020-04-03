package com.webledger.webledger.service

import com.webledger.webledger.entity.User
import com.webledger.webledger.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.stereotype.Service

@Service
class UserService (
        @Autowired
        private val userRepository: UserRepository
){
    fun register(userTo: User) {
        val user = User(userTo.username, BCryptPasswordEncoder().encode(userTo.password), userTo.name, userTo.enabled)
        userRepository.save(user)
    }
}
