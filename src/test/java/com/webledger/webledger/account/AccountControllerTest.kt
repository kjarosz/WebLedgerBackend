package com.webledger.webledger.account

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.springframework.http.HttpStatus

internal class AccountControllerTest {

    @MockK
    lateinit var accountService: AccountService

    @InjectMockKs
    lateinit var accountController: AccountController

    @Before
    fun setup() = MockKAnnotations.init(this)

    @Test
    fun `retrieve all accounts`() {
        val accounts = Iterable {
            List(2) { createTestAccount(it) }.iterator()
        }

        every { accountService.getAllAccounts() } returns accounts

        val responseEntity = accountController.getAccounts()

        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertIterableEquals(accounts, responseEntity.body)
    }

    @Test
    fun `retrieve particular account`() {
        val accountId = 1
        val account = createTestAccount(accountId)

        every { accountService.getAccount(accountId) } returns account

        val responseEntity = accountController.getAccount(1)

        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals(account, responseEntity.body)
    }

    @Test
    fun `respond with 404 when account not found`() {
        val accountId = 1

        every { accountService.getAccount(accountId) } returns null

        val responseEntity = accountController.getAccount(1)

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)
        assertNull(responseEntity.body)
    }


}