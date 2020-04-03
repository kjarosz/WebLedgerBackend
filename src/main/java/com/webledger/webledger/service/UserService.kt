package com.webledger.webledger.service

import com.webledger.webledger.entity.Authority
import com.webledger.webledger.entity.User
import com.webledger.webledger.repository.AuthorityRepository
import com.webledger.webledger.repository.UserRepository
import com.webledger.webledger.transferobject.UserTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class UserService (
        @Autowired
        private val userRepository: UserRepository,
        @Autowired
        private val authorityRepository: AuthorityRepository
){
    fun register(userTo: UserTo) {
        val user = User(userTo.username, BCryptPasswordEncoder().encode(userTo.password), userTo.name, true)
        userRepository.save(user)

        val authorities = userTo.authorities.stream()
                .map { Authority(userTo.username, it) }
                .collect(Collectors.toList())
        authorityRepository.saveAll(authorities)
    }
}
