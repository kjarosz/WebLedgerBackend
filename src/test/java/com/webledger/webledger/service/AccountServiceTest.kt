package com.webledger.webledger.service

import com.webledger.webledger.controller.GlobalExceptionHandler.AccountNotFoundException
import com.webledger.webledger.entity.Account
import com.webledger.webledger.entity.AccountType
import com.webledger.webledger.exceptions.DeleteEntityWithChildrenException
import com.webledger.webledger.repository.AccountRepository
import com.webledger.webledger.transferobject.AccountTo
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal
import java.util.*

@ExtendWith(MockKExtension::class)
internal class AccountServiceTest {

    @MockK
    lateinit var accountRepository: AccountRepository

    @InjectMockKs
    lateinit var accountService: AccountService

    @BeforeEach
    fun setup() = MockKAnnotations.init(this)

    @Test
    fun `getAllAccounts - gets all accounts`() {
        val accounts = Iterable {
            List(2) { createTestAccount(it) }.iterator()
        }

        every { accountRepository.findAll() } returns accounts

        val result = accountService.getAllAccounts()

        assertIterableEquals(accounts, result)
    }

    @Test
    fun `getAccount - gets account with particular id`() {
        val accountId = 1
        val existingAccount = createTestAccount(accountId)

        every { accountRepository.findByIdOrNull(accountId) } returns existingAccount

        val account = accountService.getAccount(accountId)

        assertEquals(existingAccount, account)
    }

    @Test
    fun `getAccount - gets null for nonexistent account`() {
        val accountId = 1

        every { accountRepository.findByIdOrNull(accountId) } returns null

        val nullAccount = accountService.getAccount(1)

        assertNull(nullAccount)
    }

    @Test
    fun `saveAccount - saves account with null id as new account`() {
        val accountTo = createTestAccountTo(null)
        val account = createTestAccount(null)
        val newAccount = createTestAccount(0)

        every { accountRepository.save(account) } returns newAccount

        val savedAccount = accountService.saveAccount(accountTo)

        verify(exactly = 1) { accountRepository.save(account) }

        assertEquals(0, savedAccount?.id)
    }

    @Test
    fun `saveAccount - saves account with nonexistent id as new account`() {
        val accountTo = createTestAccountTo(0)
        val account = createTestAccount(0)

        every { accountRepository.existsById(account.id!!) } returns false
        every { accountRepository.save(account) } returns account

        val savedAccount = accountService.saveAccount(accountTo)

        verify(exactly = 1) { accountRepository.save(account) }

        assertEquals(account, savedAccount)
    }

    @Test
    fun `saveAccount - updates existing account without changing amount`() {
        val accountTo = AccountTo(0, "Updated name", AccountType.Savings, BigDecimal.valueOf(3.0))

        var storedAccount = createTestAccount(0)
        storedAccount.amount = BigDecimal.ONE
        val optionalWrapper = Optional.of(storedAccount)
        val accountSlot = slot<Account>()

        every { accountRepository.existsById(accountTo.id!!) } returns true
        every { accountRepository.findById(storedAccount.id!!) } returns optionalWrapper
        every { accountRepository.save(capture(accountSlot)) } returns storedAccount

        accountService.saveAccount(accountTo)

        val savedAccount = accountSlot.captured
        assertEquals(accountTo.id, savedAccount.id)
        assertEquals(accountTo.name, savedAccount.name)
        assertEquals(accountTo.type, savedAccount.type)
        assertEquals(storedAccount.amount, savedAccount.amount)
        assertEquals(accountTo.limit, savedAccount.limit)
    }

    @Test
    fun `deleteAccount - non-existing account throws exception`() {
        val id = 1

        every { accountRepository.findByIdOrNull(id) } returns null

        assertThrows(AccountNotFoundException::class.java) { accountService.deleteAccount(id) }
    }

    @Test
    fun `deleteAccount - account with associated allocation centers throws exception`() {
        val id = 1
        val account = createTestAccount(id)
        account.allocationCenters = listOf(createTestAllocationCenter(id))

        every { accountRepository.findByIdOrNull(id) } returns account

        assertThrows(DeleteEntityWithChildrenException::class.java) { accountService.deleteAccount(id) }
    }

    @Test
    fun `deleteAccount - valid account is deleted`() {
        val id = 1
        val account = createTestAccount(id)

        every { accountRepository.findByIdOrNull(id) } returns account
        every { accountRepository.delete(account) } just Runs

        accountService.deleteAccount(id)

        verify(exactly = 1) { accountRepository.delete(account) }
    }
}

fun createTestAccount(index: Int?): Account {
    return Account(
            index,
            "Test $index",
            AccountType.Checking,
            BigDecimal.ZERO,
            BigDecimal.ONE,
            emptyList()
    )
}

fun createTestAccountTo(index: Int?): AccountTo {
    return AccountTo(
            index,
            "Test $index",
            AccountType.Checking,
            BigDecimal.ONE
    )
}
