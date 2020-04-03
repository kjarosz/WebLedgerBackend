package com.webledger.webledger.service

import com.webledger.webledger.entity.Authority
import com.webledger.webledger.entity.User
import com.webledger.webledger.repository.AllocationCenterRepository
import com.webledger.webledger.repository.AuthorityRepository
import com.webledger.webledger.repository.UserRepository
import com.webledger.webledger.transferobject.UserTo
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.provisioning.JdbcUserDetailsManager

@ExtendWith(MockKExtension::class)
internal class UserServiceTest {

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var authorityRepository: AuthorityRepository

    @InjectMockKs
    lateinit var userService: UserService

    lateinit var userServiceSpy: UserService

    private val username = "username"
    private val password = "password"

    @BeforeEach
    fun setup() {
        MockKAnnotations.init(this)
        userServiceSpy = spyk(userService)
    }

    @Test
    fun `register - create user with roles`() {
        val userTo = UserTo(username, password,"", listOf("authority"))
        val user = User("", "", "", true)
        val userSlot = slot<User>()
        val authoritiesSlot = slot<Iterable<Authority>>()

        every { userRepository.save(capture(userSlot)) } returns user
        every { authorityRepository.saveAll<Authority>(capture(authoritiesSlot)) } returns emptyList()

        userService.register(userTo)

        assertTrue(BCryptPasswordEncoder().matches(password, userSlot.captured.password))
        assertEquals(1, authoritiesSlot.captured.count())
    }
}
