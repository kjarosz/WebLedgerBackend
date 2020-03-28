package com.webledger.webledger.service

import com.webledger.webledger.controller.GlobalExceptionHandler.InvalidCredentialsException
import com.webledger.webledger.entity.User
import com.webledger.webledger.repository.UserRepository
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.bcrypt.BCrypt

@ExtendWith(MockKExtension::class)
internal class AuthorizationServiceTest {

    @MockK
    lateinit var userRepository: UserRepository

    @InjectMockKs
    lateinit var authorizationService: AuthorizationService

    @Before
    fun setup() = MockKAnnotations.init(this)

    @Test(expected = InvalidCredentialsException::class)
    fun `verifyUser - user not found throws InvalidCredentialsException`() {
        every { userRepository.findByUsername(any()) } returns null

        authorizationService.verifyUser("", "")
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

    @Test(expected = InvalidCredentialsException::class)
    fun `verifyUser - password does not match throws InvalidCredentialsException`() {
        val username = "user"
        val password = "hello"

        val user = User(username, BCrypt.hashpw("other", BCrypt.gensalt()), null)

        every { userRepository.findByUsername(username) } returns user

        authorizationService.verifyUser(username, password)
    }

    @Test
    fun `hashPassword - plain password returns hashed password`() {
        val plaintext = "hello"

        val hashedPassword = authorizationService.hashPassword(plaintext)

        assertTrue(BCrypt.checkpw(plaintext, hashedPassword))
    }
}

