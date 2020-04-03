package com.webledger.webledger.controller

import com.webledger.webledger.entity.AccountType
import com.webledger.webledger.entity.User
import com.webledger.webledger.service.AccountService
import com.webledger.webledger.service.UserService
import com.webledger.webledger.service.createTestAccount
import com.webledger.webledger.transferobject.AccountTo
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Assert.assertThat
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.math.BigDecimal
import javax.servlet.ServletContext

@ExtendWith(MockKExtension::class)
internal class UserControllerTest {

    @MockK
    lateinit var userService: UserService

    @MockK
    lateinit var servletContext: ServletContext

    @InjectMockKs
    lateinit var userController: UserController

    @BeforeEach
    fun setup() = MockKAnnotations.init(this)

    @Test
    fun `register - new user is registered`() {
        val user = User("username", "password", "", true)

        every { userService.register(user) } just Runs
        every { servletContext.contextPath } returns ""

        val result: ResponseEntity<Void> = userController.register(user)

        assertEquals(HttpStatus.CREATED, result.statusCode)
    }
}