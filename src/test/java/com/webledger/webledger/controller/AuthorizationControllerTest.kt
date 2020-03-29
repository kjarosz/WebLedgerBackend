package com.webledger.webledger.controller

import com.webledger.webledger.entity.User
import com.webledger.webledger.entity.WebledgerSession
import com.webledger.webledger.service.AuthorizationService
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.Before
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import java.time.LocalDateTime
import java.util.*
import javax.servlet.http.Cookie
import javax.servlet.http.HttpServletResponse

@ExtendWith(MockKExtension::class)
internal class AuthorizationControllerTest {
    @MockK
    lateinit var authorizationService: AuthorizationService

    @MockK
    lateinit var httpServletResponse: HttpServletResponse

    @InjectMockKs
    lateinit var authorizationController: AuthorizationController

    @Before
    fun setup() = MockKAnnotations.init(this)

    @Test
    fun `login - successful login returns session id in cookie`() {
        val webledgerSession = WebledgerSession(UUID.randomUUID(), User("", "", null), LocalDateTime.now())
        val sessionCookie = slot<Cookie>()

        every { authorizationService.login(any(), any()) } returns webledgerSession
        every { httpServletResponse.addCookie(capture(sessionCookie)) } just Runs

        val result = authorizationController.login("", "", httpServletResponse)

        assertEquals(webledgerSession.sessionId.toString(), sessionCookie.captured.value)
        assertEquals(HttpStatus.OK, result.statusCode)
    }
}