package com.webledger.webledger.account

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.*
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

    @Before
    fun setup() = MockKAnnotations.init(this)

    @Test
    fun `gets all accounts`() {
        val accounts = Iterable {
            List(2) { createTestAccount(it) }.iterator()
        }

        every { accountRepository.findAll() } returns accounts

        val result = accountService.getAllAccounts()

        assertIterableEquals(accounts, result)
    }

    @Test
    fun `gets account with particular id`() {
        val accountId = 1
        val existingAccount = createTestAccount(accountId)

        every { accountRepository.findByIdOrNull(accountId) } returns existingAccount

        val account = accountService.getAccount(accountId)

        assertEquals(existingAccount, account)
    }

    @Test
    fun `gets null for nonexistent account`() {
        val accountId = 1

        every { accountRepository.findByIdOrNull(accountId) } returns null

        val nullAccount = accountService.getAccount(1)

        assertNull(nullAccount)
    }

    @Test
    fun `saves account with null id as new account`() {
        val account = createTestAccount(null)
        val newAccount = createTestAccount(0)

        every { accountRepository.save(account) } returns newAccount

        val savedAccount = accountService.saveAccount(account)

        verify(exactly = 1) { accountRepository.save(account) }

        assertEquals(0, savedAccount?.id)
    }

    @Test
    fun `saves account with nonexistent id as new account`() {
        val account = createTestAccount(0)

        every { accountRepository.existsById(account.id!!) } returns false
        every { accountRepository.save(account) } returns account

        val savedAccount = accountService.saveAccount(account)

        verify(exactly = 1) { accountRepository.save(account) }

        assertEquals(0, savedAccount?.id)
    }

    @Test
    fun `updates existing account without changing amount`() {
        val accountUpdate = createTestAccount(0)
        accountUpdate.amount = BigDecimal(2.0)
        accountUpdate.name = "Updated name"
        accountUpdate.limit = BigDecimal.valueOf(3.0)

        val storedAccount = createTestAccount(0)
        val optionalWrapper = Optional.of(storedAccount)
        val accountSlot = slot<Account>()

        every { accountRepository.existsById(accountUpdate.id!!) } returns true
        every { accountRepository.findById(storedAccount.id) } returns optionalWrapper
        every { accountRepository.save(capture(accountSlot)) } returns accountUpdate

        accountService.saveAccount(accountUpdate.copy())

        assertTrue(accountSlot.isCaptured)

        val savedAccount = accountSlot.captured
        assertEquals(storedAccount.amount, savedAccount.amount)

        savedAccount.amount = accountUpdate.amount
        assertEquals(accountUpdate, savedAccount)
    }
}

fun createTestAccount(index: Int?): Account {
    return Account(
            index,
            "Test $index",
            AccountType.Checking,
            BigDecimal.valueOf(0.0),
            BigDecimal.valueOf(1.0)
    )
}
