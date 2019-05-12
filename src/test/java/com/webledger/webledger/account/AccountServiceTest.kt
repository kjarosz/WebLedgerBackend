package com.webledger.webledger.account

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertIterableEquals
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal

@ExtendWith(MockKExtension::class)
internal class AccountServiceTest {

    @MockK
    lateinit var accountRepository: AccountRepository


    @Test
    fun `gets all accounts`() {
        MockKAnnotations.init(this)
        val accountService = AccountService(accountRepository)
        val accounts = Iterable {
            List(2) {
                Account(
                    it + 1,
                    "Test {it}",
                    AccountType.Checking,
                    BigDecimal.valueOf(0.0),
                    BigDecimal.valueOf(1.0)
                )
            }.iterator()
        }

        every { accountRepository.findAll() } returns accounts

        val result = accountService.getAllAccounts()

        assertIterableEquals(accounts, result)
    }
}