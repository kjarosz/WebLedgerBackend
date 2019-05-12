package com.webledger.webledger.account

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.extension.ExtendWith
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

        val accountSlot = slot<Account>()


        every { accountRepository.existsById(accountUpdate.id!!) } returns true
        every { accountRepository.findById(storedAccount.id) } returns storedAccount
        every { accountRepository.save(capture(accountSlot)) } returns accountUpdate

        val savedAccount = accountService.saveAccount(accountUpdate)

        verify(exactly = 1) { }
    }
}

fun createTestAccount(index: Int?) = Account(
        index,
        "Test {index}",
        AccountType.Checking,
        BigDecimal.valueOf(0.0),
        BigDecimal.valueOf(1.0)
)
