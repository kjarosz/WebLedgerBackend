package com.webledger.webledger.service

import com.webledger.webledger.entity.User
import com.webledger.webledger.entity.WebledgerSession
import com.webledger.webledger.repository.UserRepository
import com.webledger.webledger.repository.WebledgerSessionRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
class AuthorizationService(
    @Autowired
    private val userRepository: UserRepository,
    @Autowired
    private val webledgerSessionRepository: WebledgerSessionRepository
): AuthenticationProvider {
    private val log: Logger = LoggerFactory.getLogger(AuthorizationService::class.java.simpleName)

    fun hashPassword(plaintext: String): String {
        val salt = BCrypt.gensalt()
        return BCrypt.hashpw(plaintext, salt)
    }

    fun verifyUser(username: String, password: String): User {
        val user = userRepository.findByUsername(username)
        if (user != null && BCrypt.checkpw(password, user.password)) {
            return user
        } else {
            if (user == null) {
                log.debug("User: {} not found in the repository")
            } else {
                log.debug("Invalid password for user: {}", username)
            }
            throw BadCredentialsException("No match found for provided credentials")
        }
    }

    fun login(username: String, password: String): WebledgerSession {
        val user = verifyUser(username, password)
        val webledgerSession = WebledgerSession(UUID.randomUUID(), user, LocalDateTime.now())
        webledgerSessionRepository.save(webledgerSession)
        return webledgerSession
    }

    override fun authenticate(authentication: Authentication?): Authentication? {
        log.info("Authenticating user: {}", authentication?.name)
        val user = verifyUser(authentication!!.name, authentication!!.credentials.toString())
        log.info("User: {} successfully authenticated", authentication!!.name)
        return UsernamePasswordAuthenticationToken(user.name, user.password, ArrayList())
    }

    override fun supports(authentication: Class<*>?): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.equals(authentication)
    }
}
