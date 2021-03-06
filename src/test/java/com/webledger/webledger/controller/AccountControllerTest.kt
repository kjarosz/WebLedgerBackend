package com.webledger.webledger.controller

import com.webledger.webledger.entity.AccountType
import com.webledger.webledger.service.AccountService
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

@ExtendWith(MockKExtension::class)
internal class AccountControllerTest {

    @MockK
    lateinit var accountService: AccountService

    @InjectMockKs
    lateinit var accountController: AccountController

    @BeforeEach
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
    fun `saveAccount - Returns 201 success when account is saved successfully`() {
        val accountId = 1
        val accountTo = AccountTo(null, "New Account.kt", AccountType.Checking, BigDecimal.ZERO)
        val savedAccount = createTestAccount(accountId)

        every { accountService.saveAccount(accountTo) } returns savedAccount

        val responseEntity = accountController.saveAccount(accountTo)

        assertEquals(HttpStatus.CREATED, responseEntity.statusCode)
        assertThat(responseEntity.headers["Location"]?.get(0), containsString("/accounts/$accountId"))
    }

    @Test
    fun `deleteAccount - Returns 204 no content when account is deleted successfully`() {
        val id = 1

        every { accountService.deleteAccount(id) } just Runs

        val responseEntity: ResponseEntity<Void> = accountController.deleteAccount(id)

        verify(exactly = 1) { accountService.deleteAccount(id) }

        assertEquals(HttpStatus.NO_CONTENT, responseEntity.statusCode)
    }

    @Test
    fun `getAccountTypes - Returns list of account types`() {
        val responseEntity = accountController.getAccountTypes()

        assertArrayEquals(AccountType.values(), responseEntity.body)
        assertThat(HttpStatus.OK, equalTo(responseEntity.statusCode))
    }
}