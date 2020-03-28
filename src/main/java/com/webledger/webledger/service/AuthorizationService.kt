package com.webledger.webledger.service

import com.webledger.webledger.controller.GlobalExceptionHandler.InvalidCredentialsException
import com.webledger.webledger.entity.User
import com.webledger.webledger.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service

@Service
class AuthorizationService(
    @Autowired
    private val userRepository: UserRepository
) {
    fun hashPassword(plaintext: String): String {
        val salt = BCrypt.gensalt()
        return BCrypt.hashpw(plaintext, salt)
    }

    fun verifyUser(username: String, password: String): User {
        val user = userRepository.findByUsername(username)
        if (user != null && BCrypt.checkpw(password, user.password)) {
            return user
        } else {
            throw InvalidCredentialsException("No match found for provided credentials")
        }
    }
}
