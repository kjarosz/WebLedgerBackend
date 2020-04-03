package com.webledger.webledger.service

import com.webledger.webledger.entity.User
import com.webledger.webledger.entity.WebledgerSession
import com.webledger.webledger.repository.UserRepository
import com.webledger.webledger.repository.WebledgerSessionRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.spyk
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCrypt
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
internal class AuthorizationServiceTest {

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var webledgerSessionRepository: WebledgerSessionRepository

    @InjectMockKs
    lateinit var authorizationService: AuthorizationService

    lateinit var authorizationServiceSpy: AuthorizationService

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        authorizationServiceSpy = spyk(authorizationService)
    }

    @Test
    fun `login - verified user creates new session`() {
        val user = User("", "", null)

        every { authorizationServiceSpy.verifyUser(any(), any()) } returns user
        every {
            webledgerSessionRepository.save<WebledgerSession>(any())
        } returns WebledgerSession(UUID.randomUUID(), user, LocalDateTime.MAX)

        val result = authorizationServiceSpy.login("", "")

        assertNotNull(result)
    }

    @Test
    fun `verifyUser - user not found throws InvalidCredentialsException`() {
        every { userRepository.findByUsername(any()) } returns null

        assertThrows(BadCredentialsException::class.java) { authorizationService.verifyUser("", "") }
    }

    @Test
    fun `verifyUser - username and password match returns user data`() {
        val username = "user"
        val password = "hello"

        val user = User(username, BCrypt.hashpw(password, BCrypt.gensalt()), null)

        every { userRepository.findByUsername(username) } returns user

        val result = authorizationService.verifyUser(username, password)

        assertEquals(user, result)
    }

    @Test
    fun `verifyUser - password does not match throws InvalidCredentialsException`() {
        val username = "user"
        val password = "hello"

        val user = User(username, BCrypt.hashpw("other", BCrypt.gensalt()), null)

        every { userRepository.findByUsername(username) } returns user

        assertThrows(BadCredentialsException::class.java) { authorizationService.verifyUser(username, password) }
    }

    @Test
    fun `hashPassword - plain password returns hashed password`() {
        val plaintext = "hello"

        val hashedPassword = authorizationService.hashPassword(plaintext)

        assertTrue(BCrypt.checkpw(plaintext, hashedPassword))
    }

    @Test
    fun `authenticate - verified user returns authentication token`() {
        val username = "user"
        val password = "pass"
        val authentication = UsernamePasswordAuthenticationToken(username, password)
        val user = User(username, BCrypt.hashpw(password, BCrypt.gensalt()), null)

        every { authorizationServiceSpy.verifyUser(username, password) } returns user

        val result = authorizationServiceSpy.authenticate(authentication)

        assertTrue(result!!.isAuthenticated)
    }
}

