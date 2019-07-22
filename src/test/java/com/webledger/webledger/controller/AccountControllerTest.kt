package com.webledger.webledger.controller

import com.webledger.webledger.service.AccountService
import com.webledger.webledger.transferobject.AccountTo
import com.webledger.webledger.entity.AccountType
import com.webledger.webledger.service.createTestAccount
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
internal class AccountControllerTest {

    @MockK
    lateinit var accountService: AccountService

    @InjectMockKs
    lateinit var accountController: AccountController

    @Before
    fun setup() = MockKAnnotations.init(this)

    @Test
    fun `getAccounts - Returns list of accounts when asked for it`() {
        val accounts = Iterable {
            List(2) { createTestAccount(it) }.iterator()
        }

        every { accountService.getAllAccounts() } returns accounts

        val responseEntity = accountController.getAccounts()

        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertIterableEquals(accounts, responseEntity.body)
    }

    @Test
    fun `getAccount - Returns account when a valid accountId is given`() {
        val accountId = 1
        val account = createTestAccount(accountId)

        every { accountService.getAccount(accountId) } returns account

        val responseEntity = accountController.getAccount(1)

        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals(account, responseEntity.body)
    }

    @Test
    fun `getAccount - Returns 404 error and null body when invalid accountId is given`() {
        val accountId = 1

        every { accountService.getAccount(accountId) } returns null

        val responseEntity = accountController.getAccount(1)

        assertEquals(HttpStatus.NOT_FOUND, responseEntity.statusCode)
        assertNull(responseEntity.body)
    }

    @Test
    fun `saveAccount - Returns 200 success when account is saved successfully`() {
        val accountId = 1
        val accountTo = AccountTo(null, "New Account.kt", AccountType.Checking, BigDecimal.ZERO)
        val savedAccount = createTestAccount(accountId)

        every { accountService.saveAccount(accountTo) } returns savedAccount

        val responseEntity = accountController.saveAccount(accountTo)

        assertEquals(HttpStatus.OK, responseEntity.statusCode)
        assertEquals(savedAccount, responseEntity.body)
    }
}